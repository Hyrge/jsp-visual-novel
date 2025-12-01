package manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dao.CommentDAO;
import dao.PostDAO;
import dto.Comment;
import dto.Post;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 게시글/댓글 관리자
 * - JSON 초기 데이터 로드 (post.json, mina_post.json, comment.json)
 * - DB 동적 데이터와 병합
 */
public class PostManager {
    private final PostDAO postDAO;
    private final CommentDAO commentDAO;
    private final Map<String, Post> initialPosts;
    private final Map<String, List<Comment>> initialComments;

    public PostManager() {
        this.postDAO = new PostDAO();
        this.commentDAO = new CommentDAO();
        this.initialPosts = new HashMap<>();
        this.initialComments = new HashMap<>();

        loadInitialData();
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
            }

            // mina_post.json 로드
            InputStream minaStream = getClass().getClassLoader().getResourceAsStream("mina_post.json");
            if (minaStream != null) {
                List<Post> minaPosts = mapper.readValue(minaStream, new TypeReference<List<Post>>() {});
                minaPosts.forEach(p -> initialPosts.put(p.getPostId(), p));
            }

            // comment.json 로드
            InputStream commentStream = getClass().getClassLoader().getResourceAsStream("comment.json");
            if (commentStream != null) {
                List<Comment> comments = mapper.readValue(commentStream, new TypeReference<List<Comment>>() {});
                for (Comment c : comments) {
                    initialComments.computeIfAbsent(c.getPostId(), k -> new ArrayList<>()).add(c);
                }
            }

        } catch (Exception e) {
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
        Map<String, Post> resultMap = new HashMap<>();

        // 1. JSON 초기 데이터 먼저 추가
        initialPosts.values().stream()
            .filter(p -> boardType.equals(p.getBoardType()))
            .forEach(p -> resultMap.put(p.getPostId(), p));

        // 2. DB 동적 데이터 추가 (내용이 있는 것만)
        List<Post> dbPosts = postDAO.findByBoardType(boardType);
        for (Post dbPost : dbPosts) {
            if (dbPost.getContent() != null && !dbPost.getContent().isEmpty()) {
                resultMap.put(dbPost.getPostId(), dbPost);
            }
        }

        // 3. 정렬 (최신순)
        return resultMap.values().stream()
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .collect(Collectors.toList());
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
}
