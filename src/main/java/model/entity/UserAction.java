package model.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import model.enums.ActionType;

/**
 * 플레이어의 행동을 나타내는 엔티티
 */
public class UserAction {
    private ActionType actionType;
    private String targetId;        // postId, commentId 등
    private String title;           // 게시글 제목 (게시글 작성 시)
    private String content;         // 내용
    private String playerId;        // 행동한 플레이어 ID
    private LocalDateTime timestamp;
    private Map<String, Object> metadata; // 추가 메타데이터


    public UserAction() {
        this.metadata = new HashMap<>();
        this.timestamp = LocalDateTime.now();
    }

    public UserAction(ActionType actionType, String targetId, String playerId) {
        this();
        this.actionType = actionType;
        this.targetId = targetId;
        this.playerId = playerId;
    }

    // Getters and Setters
    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
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

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    public Object getMetadata(String key) {
        return this.metadata.get(key);
    }
}
