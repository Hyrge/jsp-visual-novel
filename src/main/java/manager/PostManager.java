package manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dao.CommentDAO;
import dao.PostDAO;
import dto.Comment;
import dto.Post;
import model.NPCUser;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 게시글/댓글 관리자 (싱글톤)
 * - JSON 초기 데이터 로드 (post.json, mina_post.json, comment.json)
 * - DB 동적 데이터와 병합
 */
public class PostManager {
    private static final PostManager instance = new PostManager();

    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final Map<String, Post> initialPosts;
    private final Map<String, List<Comment>> initialComments;

    private PostManager() {
        this.postDAO = new PostDAO();
        this.commentDAO = new CommentDAO();
        this.initialPosts = new HashMap<>();
        this.initialComments = new HashMap<>();

        loadInitialData();
    }

    public static PostManager getInstance() {
        return instance;
    }

    private void loadInitialData() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            // post.json 로드
            InputStream postStream = getClass().getClassLoader().getResourceAsStream("post.json");
            if (postStream != null) {
                List<Post> posts = mapper.readValue(postStream, new TypeReference<List<Post>>() {});
                posts.forEach(p -> initialPosts.put(p.getPostId(), p));
                System.out.println("PostManager: Loaded " + posts.size() + " posts from post.json");
            } else {
                System.err.println("PostManager: post.json not found!");
            }

            // mina_post.json 로드
            InputStream minaStream = getClass().getClassLoader().getResourceAsStream("mina_post.json");
            if (minaStream != null) {
                List<Post> minaPosts = mapper.readValue(minaStream, new TypeReference<List<Post>>() {});
                minaPosts.forEach(p -> initialPosts.put(p.getPostId(), p));
                System.out.println("PostManager: Loaded " + minaPosts.size() + " posts from mina_post.json");
            } else {
                System.err.println("PostManager: mina_post.json not found!");
            }

            // comment.json 로드
            InputStream commentStream = getClass().getClassLoader().getResourceAsStream("comment.json");
            if (commentStream != null) {
                List<Comment> comments = mapper.readValue(commentStream, new TypeReference<List<Comment>>() {});
                for (Comment c : comments) {
                    initialComments.computeIfAbsent(c.getPostId(), k -> new ArrayList<>()).add(c);
                }
                System.out.println("PostManager: Loaded " + comments.size() + " comments from comment.json");
            } else {
                System.err.println("PostManager: comment.json not found!");
            }

            System.out.println("PostManager: Total posts loaded: " + initialPosts.size());

        } catch (Exception e) {
            System.err.println("PostManager: Error loading initial data");
            e.printStackTrace();
        }
    }

    /**
     * 게시글 조회 (JSON + DB 병합)
     */
    public Post getPost(String postId) {
        // JSON에서 먼저 조회 (초기 데이터)
        Post jsonPost = initialPosts.get(postId);
        if (jsonPost != null) {
            return jsonPost;
        }

        // JSON에 없으면 DB에서 조회 (동적 데이터)
        return postDAO.findById(postId);
    }

    /**
     * 게시판별 게시글 목록 조회 (JSON + DB 병합)
     */
    public List<Post> getPostsByBoardType(String boardType) {
        return getPostsByBoardType(boardType, null);
    }

    /**
     * 게시판별 게시글 목록 조회 (시간 필터링 포함)
     */
    public List<Post> getPostsByBoardType(String boardType, java.time.LocalDateTime currentTime) {
        Map<String, Post> resultMap = new HashMap<>();

        // 1. JSON 초기 데이터 먼저 추가
        long jsonCount = initialPosts.values().stream()
            .filter(p -> boardType.equals(p.getBoardType()))
            .filter(p -> currentTime == null || !p.getCreatedAt().isAfter(currentTime)) // 시간 필터링
            .peek(p -> resultMap.put(p.getPostId(), p))
            .count();
        System.out.println("PostManager: Found " + jsonCount + " posts from JSON for board type: " + boardType);

        // 2. DB 동적 데이터 추가 (내용이 있고, JSON에 없는 것만 추가)
        try {
            List<Post> dbPosts = postDAO.findByBoardType(boardType);
            int dbCount = 0;
            for (Post dbPost : dbPosts) {
                // JSON에 이미 있는 데이터는 건드리지 않음 (JSON이 완전한 데이터)
                // 시간 필터링도 적용
                if (!resultMap.containsKey(dbPost.getPostId())
                    && dbPost.getContent() != null
                    && !dbPost.getContent().isEmpty()
                    && (currentTime == null || !dbPost.getCreatedAt().isAfter(currentTime))) {
                    resultMap.put(dbPost.getPostId(), dbPost);
                    dbCount++;
                }
            }
            System.out.println("PostManager: Found " + dbCount + " NEW posts from DB for board type: " + boardType);
        } catch (Exception e) {
            System.err.println("PostManager: Error loading DB posts: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. 정렬 (최신순)
        List<Post> result = resultMap.values().stream()
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .collect(Collectors.toList());

        System.out.println("PostManager: Returning " + result.size() + " total posts (time-filtered)");
        return result;
    }

    /**
     * 댓글 목록 조회 (JSON + DB 병합)
     */
    public List<Comment> getComments(String postId) {
        List<Comment> result = new ArrayList<>();

        // JSON 초기 데이터
        List<Comment> jsonComments = initialComments.get(postId);
        if (jsonComments != null) {
            result.addAll(jsonComments);
        }

        // DB 동적 데이터
        List<Comment> dbComments = commentDAO.findByPostId(postId);
        result.addAll(dbComments);

        // 정렬 (comment_seq, created_at)
        result.sort((c1, c2) -> {
            int seqCompare = Integer.compare(c1.getCommentSeq(), c2.getCommentSeq());
            if (seqCompare != 0) return seqCompare;
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
        // comment_seq 자동 설정
        if (comment.getCommentSeq() == 0) {
            int nextSeq = commentDAO.getNextCommentSeq(comment.getPostId());
            comment.setCommentSeq(nextSeq);
        }

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
            return "익명" + npcId.substring(0, 4);
        }

        // postId와 npcId를 조합하여 시드 생성 (같은 게시물에서는 같은 닉네임)
        int seed = (postId + npcId).hashCode();
        Random random = new Random(seed);
        List<String> pool = npc.getNicknamePool();
        return pool.get(random.nextInt(pool.size()));
    }

    /**
     * 모든 게시글 조회 (talk 게시판용)
     */
    public List<Post> getAllPosts() {
        return getPostsByBoardType("talk", null);
    }

    /**
     * 모든 게시글 조회 (시간 필터링 포함)
     */
    public List<Post> getAllPosts(java.time.LocalDateTime currentTime) {
        return getPostsByBoardType("talk", currentTime);
    }

    /**
     * 특정 게시글의 댓글 목록 조회 (별칭)
     */
    public List<Comment> getCommentsByPostId(String postId) {
        return getComments(postId);
    }
}
