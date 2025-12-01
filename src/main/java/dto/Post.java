package dto;

import java.time.LocalDateTime;

public class Post {
    private String postId;
    private String authorPid;
    private String title;
    private String content;
    private String boardType;
    private String category;
    private LocalDateTime createdAt;
    private boolean hasPictures;
    private int likeCount;
    private int dislikeCount;
    private boolean isRelatedMina;

    public Post() {}

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBoardType() {
        return boardType;
    }

    public void setBoardType(String boardType) {
        this.boardType = boardType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isHasPictures() {
        return hasPictures;
    }

    public void setHasPictures(boolean hasPictures) {
        this.hasPictures = hasPictures;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public boolean isRelatedMina() {
        return isRelatedMina;
    }

    public void setRelatedMina(boolean relatedMina) {
        isRelatedMina = relatedMina;
    }
}
