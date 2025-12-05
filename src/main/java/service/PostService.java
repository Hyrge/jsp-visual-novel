package service;

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
import model.NPCUser;

/**
 * 게시글/댓글 서비스
 * - JSON 초기 데이터와 DB 동적 데이터 병합
 */
public class PostService {
    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final DataManager dataManager;

    public PostService(DataManager dataManager) {
        if (dataManager == null) {
            throw new IllegalArgumentException("dataManager는 필수입니다.");
        }
        this.dataManager = dataManager;
        this.postDAO = new PostDAO();
        this.commentDAO = new CommentDAO();
    }

    /**
     * 게시글 조회 (JSON + DB 병합)
     */
    public Post getPost(String postId) {
        // JSON에서 먼저 조회 (초기 데이터)
        Post jsonPost = dataManager.getInitialPosts().get(postId);
        if (jsonPost != null) {
            return jsonPost;
        }

        // JSON에 없으면 DB에서 조회 (동적 데이터)
        return postDAO.findById(postId);
    }

    /**
     * 게시판별 게시글 목록 조회 (시간 필터링 + 플레이어 필터링 필수)
     */
    public List<Post> getPostsByBoardType(String boardType, java.time.LocalDateTime currentTime, String playerPid) {
        if (currentTime == null) {
            throw new IllegalArgumentException("currentTime은 필수입니다");
        }
        if (playerPid == null) {
            throw new IllegalArgumentException("playerPid는 필수입니다");
        }

        Map<String, Post> resultMap = new HashMap<>();

        // 1. JSON 초기 데이터 먼저 추가 (모든 플레이어 공통)
        long jsonCount = dataManager.getInitialPosts().values().stream()
            .filter(p -> boardType.equals(p.getBoardType()))
            .filter(p -> currentTime == null || !p.getCreatedAt().isAfter(currentTime))
            .peek(p -> resultMap.put(p.getPostId(), p))
            .count();
        System.out.println("PostService: Found " + jsonCount + " posts from JSON for board type: " + boardType);

        // 2. DB 동적 데이터 추가 (playerPid로 필터링, JSON에 없는 것만)
        try {
            List<Post> dbPosts = postDAO.findByBoardType(boardType, playerPid);
            int dbCount = 0;
            for (Post dbPost : dbPosts) {
                if (!resultMap.containsKey(dbPost.getPostId())
                    && dbPost.getContent() != null
                    && !dbPost.getContent().isEmpty()
                    && (currentTime == null || !dbPost.getCreatedAt().isAfter(currentTime))) {
                    resultMap.put(dbPost.getPostId(), dbPost);
                    dbCount++;
                }
            }
            System.out.println("PostService: Found " + dbCount + " NEW posts from DB for board type: " + boardType + " (playerPid: " + playerPid + ")");
        } catch (Exception e) {
            System.err.println("PostService: Error loading DB posts: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. 정렬 (최신순)
        List<Post> result = resultMap.values().stream()
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .collect(Collectors.toList());

        System.out.println("PostService: Returning " + result.size() + " total posts");
        return result;
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

        // 3. 정렬 (comment_seq, created_at)
        List<Comment> result = new ArrayList<>(resultMap.values());
        result.sort((c1, c2) -> {
            int seqCompare = Integer.compare(c1.getCommentSeq(), c2.getCommentSeq());
            if (seqCompare != 0) {
				return seqCompare;
			}
            return c1.getCreatedAt().compareTo(c2.getCreatedAt());
        });

        return result;
    }

    /**
     * 새 게시글 작성 (DB에 저장)
     */
    public boolean createPost(Post post) {
        return postDAO.insert(post);
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




