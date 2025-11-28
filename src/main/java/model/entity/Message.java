package model.entity;

import java.time.LocalDateTime;
import java.util.List;
import model.enums.SenderType;

public class Message {
    private String id;
    private SenderType sender; // SYSTEM, ADMIN, COMPANY
    private String title;
    private String content;
    private boolean read;
    private LocalDateTime createdAt;
    private String relatedEventId;
    private List<String> relatedQuestIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SenderType getSender() {
        return sender;
    }

    public void setSender(SenderType sender) {
        this.sender = sender;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRelatedEventId() {
        return relatedEventId;
    }

    public void setRelatedEventId(String relatedEventId) {
        this.relatedEventId = relatedEventId;
    }

    public List<String> getRelatedQuestIds() {
        return relatedQuestIds;
    }

    public void setRelatedQuestIds(List<String> relatedQuestIds) {
        this.relatedQuestIds = relatedQuestIds;
    }
}
