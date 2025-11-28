package model.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import model.enums.EventCategory;
import model.enums.EventStatus;
import model.enums.EventType;

public class Event {
    private String id;
    private EventType type;
    private EventCategory category; // POSITIVE, NEGATIVE
    private int severity; // 1~5
    private String title;
    private String description;
    private EventStatus status; // ACTIVE, RESOLVED, EXPIRED
    private LocalDate triggeredAt;
    private LocalTime triggeredTime;
    private int immediateReputation; // 발생 시 즉시 평판 변화

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public EventCategory getCategory() {
        return category;
    }

    public void setCategory(EventCategory category) {
        this.category = category;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public LocalDate getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(LocalDate triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public void setTriggeredTime(LocalTime triggeredTime) {
        this.triggeredTime = triggeredTime;
    }

    public LocalTime getTriggeredTime() {
        return triggeredTime;
    }

    public int getImmediateReputation() {
        return immediateReputation;
    }

    public void setImmediateReputation(int immediateReputation) {
        this.immediateReputation = immediateReputation;
    }
}
