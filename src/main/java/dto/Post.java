package dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Post {
    @JsonProperty("post_id")
    private String postId;

    @JsonProperty("player_pid")
    private String playerPid;

    private String title;
    private String content;

    @JsonProperty("board_type")
    private String boardType;

    private String category;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("has_pictures")
    private boolean hasPictures;

    @JsonProperty("like_count")
    private int likeCount;

    @JsonProperty("dislike_count")
    private int dislikeCount;

    @JsonProperty("is_related_mina")
    private boolean isRelatedMina;

    @JsonProperty("nickname")
    private String nickname; // 작성자 닉네임 (NPC는 풀에서 선택)

    @JsonProperty("image_file")
    private String imageFile; // 이미지 파일명 (saves/{pid}/ 경로에 저장)

    public Post() {
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

    public String getNickname() {
        return nickname;
    }

    public void setNickName(String nickname) {
        this.nickname = nickname;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
