package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import dao.CommentDAO;
import dao.PostDAO;
import dto.Comment;
import dto.Post;
import manager.DataManager;
import manager.NPCUserManager;
import model.EventBus;
import model.GameState;
import model.NPCUser;
import model.enums.BusEvent;

/**
 * 게시글/댓글 서비스
 * - JSON 초기 데이터와 DB 동적 데이터 병합
 * - totalPosts: 모든 게시글 (시간 상관없이)
 * - publishedPosts: 현재 시점에 게시판에 보이는 게시글
 */
public class PostService {
    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final DataManager dataManager;
    private EventBus eventBus;
    private GameState gameState;

    // 모든 게시글 (시간 상관없이)
    private Map<String, Post> totalPosts;
    // 현재 시점에 게시판에 보이는 게시글
    private Map<String, Post> publishedPosts;

    public PostService(DataManager dataManager) {
        if (dataManager == null) {
            throw new IllegalArgumentException("dataManager는 필수입니다.");
        }
        this.dataManager = dataManager;
        this.postDAO = new PostDAO();
        this.commentDAO = new CommentDAO();
        this.totalPosts = new HashMap<>();
        this.publishedPosts = new HashMap<>();
    }

    /**
     * EventBus, GameState 설정 (GameContext에서 호출)
     */
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        // TIME_ADVANCED 이벤트 구독
        eventBus.subscribe(BusEvent.TIME_ADVANCED, this::onTimeAdvanced);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * TIME_ADVANCED 이벤트 핸들러 - 해당 시간의 게시글 publish
     */
    private void onTimeAdvanced(Object data) {
        Map<String, Object> timeData = (Map<String, Object>) data;
        LocalDateTime afterTime = (LocalDateTime) timeData.get("after");
        publishPostAt(afterTime);
    }

    /**
     * 게시글 초기화 (JSON + DB 로드 → totalPosts, 이벤트 시간 등록)
     */
    public void initPosts(String playerPid) {
        // 게임 시작 시간 (9월 1일 09:00)
        LocalDateTime gameStartTime = LocalDateTime.of(2025, 9, 1, 9, 0);

        // 1. JSON 초기 데이터 로드
        totalPosts.putAll(dataManager.getInitialPosts());
        System.out.println("[PostService] JSON에서 " + dataManager.getInitialPosts().size() + "개 게시글 로드");

        // 2. DB 동적 데이터 로드
        try {
            List<Post> dbPosts = postDAO.findByBoardType("talk", playerPid);
            for (Post dbPost : dbPosts) {
                if (dbPost.getContent() != null && !dbPost.getContent().isEmpty()) {
                    totalPosts.put(dbPost.getPostId(), dbPost);
                }
            }
            System.out.println("[PostService] DB에서 " + dbPosts.size() + "개 게시글 로드");
        } catch (Exception e) {
            System.err.println("[PostService] DB 게시글 로드 오류: " + e.getMessage());
        }

        // 3. 각 게시글 처리
        for (Post post : totalPosts.values()) {
            if (!post.getCreatedAt().isAfter(gameStartTime)) {
                // 게임 시작 시간 이전 게시글 → 바로 publishedPosts에 추가
                publishedPosts.put(post.getPostId(), post);
            } else {
                // 게임 시작 시간 이후 게시글 → 이벤트 시간에 등록
                if (gameState != null) {
                    gameState.addEventTime(post.getCreatedAt());
                }
            }
        }

        System.out.println("[PostService] publishedPosts: " + publishedPosts.size() + "개, 이벤트 시간 등록: " + (totalPosts.size() - publishedPosts.size()) + "개");
        System.out.println("[PostService] totalPosts 초기화 완료: " + totalPosts.size() + "개");
    }

    /**
     * 해당 시간에 맞는 게시글을 publishedPosts에 추가 (POST_CREATED 이벤트 발행)
     */
    public void publishPostAt(LocalDateTime time) {
        for (Post post : totalPosts.values()) {
            // 아직 publish 안 된 것 중에서 해당 시간에 맞는 것만
            if (!publishedPosts.containsKey(post.getPostId())
                && post.getCreatedAt().equals(time)) {
                publishPost(post);
            }
        }
    }

    /**
     * 게시글을 publishedPosts에 추가하고 POST_CREATED 이벤트 발행
     */
    private void publishPost(Post post) {
        publishedPosts.put(post.getPostId(), post);
        System.out.println("[PostService] 게시글 publish: " + post.getTitle());

        // POST_CREATED 이벤트 발행
        if (eventBus != null) {
            eventBus.emit(BusEvent.POST_CREATED, Map.of(
                "postId", post.getPostId(),
                "title", post.getTitle(),
                "content", post.getContent(),
                "isRelatedMina", post.isRelatedMina()
            ));
        }
    }

    /**
     * 게시글 조회 (publishedPosts에서 조회)
     */
    public Post getPost(String postId) {
        return publishedPosts.get(postId);
    }

    /**
     * 게시판별 게시글 목록 조회 (publishedPosts에서 필터링)
     */
    public List<Post> getPostsByBoardType(String boardType, LocalDateTime currentTime, String playerPid) {
        // publishedPosts에서 boardType으로 필터링 후 최신순 정렬
        return publishedPosts.values().stream()
            .filter(p -> boardType.equals(p.getBoardType()))
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .collect(Collectors.toList());
    }

    /**
     * 댓글 목록 조회 (JSON + DB 병합, 플레이어 + 시간 필터링 필수)
     */
    public List<Comment> getComments(String postId, String playerPid, java.time.LocalDateTime currentTime) {

        Map<Integer, Comment> resultMap = new HashMap<>();

        // 1. JSON 초기 데이터 먼저 추가 (시간 필터링)
        List<Comment> jsonComments = dataManager.getInitialComments().get(postId);
        if (jsonComments != null) {
            for (Comment c : jsonComments) {
                if (currentTime == null || !c.getCreatedAt().isAfter(currentTime)) {
                    resultMap.put(c.getCommentId(), c);
                }
            }
        }

        // 2. DB 동적 데이터 추가 (playerPid + 시간 필터링)
        List<Comment> dbComments = commentDAO.findByPostId(postId, playerPid, currentTime);
        for (Comment c : dbComments) {
            if (!resultMap.containsKey(c.getCommentId())
                && c.getContent() != null
                && !c.getContent().isEmpty()) {
                resultMap.put(c.getCommentId(), c);
            }
        }

        // 3. 정렬 (created_at 오름차순)
        List<Comment> result = new ArrayList<>(resultMap.values());
        result.sort((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()));

        return result;
    }

    /**
     * 새 게시글 작성 (DB 저장 + totalPosts + publishedPosts 추가)
     * 플레이어가 작성한 글은 즉시 게시되므로 둘 다 추가하고 POST_CREATED 이벤트 발행
     */
    public boolean createPost(Post post) {
        boolean dbResult = postDAO.insert(post);
        if (dbResult) {
            // totalPosts에 추가
            totalPosts.put(post.getPostId(), post);
            // publishedPosts에 추가 + 이벤트 발행
            publishPost(post);
        }
        return dbResult;
    }

    /**
     * 새 댓글 작성 (DB에 저장)
     */
    public boolean createComment(Comment comment) {
        return commentDAO.insert(comment);
    }

    /**
     * 게시글 수정
     */
    public boolean updatePost(Post post) {
        return postDAO.update(post);
    }

    /**
     * 게시글 삭제
     */
    public boolean deletePost(String postId) {
        return postDAO.delete(postId);
    }

    /**
     * 게시글의 MiNa 관련 여부 업데이트
     */
    public boolean updatePostMinaRelated(String postId, boolean isRelatedMina) {
        return postDAO.updateMinaRelated(postId, isRelatedMina);
    }

    /**
     * 댓글 삭제
     */
    public boolean deleteComment(int commentId) {
        return commentDAO.delete(commentId);
    }

    /**
     * NPC ID로부터 랜덤 닉네임 선택
     * (게시물별로 일관된 닉네임 사용하도록 postId 기반 시드 사용)
     */
    public String assignNicknameForNPC(String npcId, String postId) {
        NPCUser npc = NPCUserManager.getInstance().getNPCById(npcId);
        if (npc == null || npc.getNicknamePool() == null || npc.getNicknamePool().isEmpty()) {
            return "유저";
        }

        // postId와 npcId를 조합하여 시드 생성 (같은 게시물에서는 같은 닉네임)
        int seed = (postId + npcId).hashCode();
        Random random = new Random(seed);
        List<String> pool = npc.getNicknamePool();
        return pool.get(random.nextInt(pool.size()));
    }

    /**
     * 모든 게시글 조회 (talk 게시판용, currentTime + playerPid 필수)
     */
    public List<Post> getAllPosts(java.time.LocalDateTime currentTime, String playerPid) {
        return getPostsByBoardType("talk", currentTime, playerPid);
    }

    /**
     * 특정 게시글의 댓글 목록 조회 (currentTime + playerPid 필수)
     */
    public List<Comment> getCommentsByPostId(String postId, String playerPid, java.time.LocalDateTime currentTime) {
        return getComments(postId, playerPid, currentTime);
    }
}




