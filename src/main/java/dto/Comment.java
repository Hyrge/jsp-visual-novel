package dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Comment {
    @JsonProperty("comment_id")
    private int commentId;

    @JsonProperty("comment_seq")
    private int commentSeq;

    @JsonProperty("post_id")
    private String postId;

    @JsonProperty("player_pid")
    private String playerPid;

    private String content;

    @JsonProperty("parent_comment_id")
    private Integer parentCommentId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("nickname")
    private String nickname;

    public Comment() {
    }

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

    public String getPlayerPid() {
        return playerPid;
    }

    public void setPlayerPid(String playerPid) {
        this.playerPid = playerPid;
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
