package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Comment {
    @JsonProperty("comment_id")
    private int commentId;

    @JsonProperty("comment_seq")
    private int commentSeq;

    @JsonProperty("post_id")
    private String postId;

    @JsonProperty("author_pid")
    private String authorPid;

    private String content;

    @JsonProperty("parent_comment_id")
    private Integer parentCommentId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("author_nickname")
    private String authorNickname; // 작성자 닉네임 (NPC는 풀에서 선택)

    public Comment() {}

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getCommentSeq() {
        return commentSeq;
    }

    public void setCommentSeq(int commentSeq) {
        this.commentSeq = commentSeq;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorPid() {
        return authorPid;
    }

    public void setAuthorPid(String authorPid) {
        this.authorPid = authorPid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Integer parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }
}
