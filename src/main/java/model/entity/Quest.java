package model.entity;

import java.time.LocalDateTime;
import model.enums.QuestIssuer;
import model.enums.QuestOrigin;
import model.enums.QuestStatus;

public class Quest {
    private String id;
    private QuestOrigin origin; // EVENT, COMPANY, SYSTEM, DAILY
    private String originId;
    private QuestIssuer issuer; // SYSTEM, COMPANY
    private String title;
    private String description;
    private boolean required;
    private QuestStatus status; // AVAILABLE, IN_PROGRESS, COMPLETABLE, COMPLETED, FAILED
    private LocalDateTime deadline;

    // 단일 목표
    private int currentProgress;
    private int requiredProgress;

    // 평판 보상/페널티
    private int rewardReputation;
    private int penaltyReputation;

    // 체이닝
    private String nextQuestId;
    private int sequence;

    public boolean isComplete() {
        return currentProgress >= requiredProgress;
    }

    public void addProgress(int amount) {
        this.currentProgress = Math.min(currentProgress + amount, requiredProgress);
    }

    public boolean hasNextQuest() {
        return nextQuestId != null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public QuestOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(QuestOrigin origin) {
        this.origin = origin;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public QuestIssuer getIssuer() {
        return issuer;
    }

    public void setIssuer(QuestIssuer issuer) {
        this.issuer = issuer;
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public void setStatus(QuestStatus status) {
        this.status = status;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getRequiredProgress() {
        return requiredProgress;
    }

    public void setRequiredProgress(int requiredProgress) {
        this.requiredProgress = requiredProgress;
    }

    public int getRewardReputation() {
        return rewardReputation;
    }

    public void setRewardReputation(int rewardReputation) {
        this.rewardReputation = rewardReputation;
    }

    public int getPenaltyReputation() {
        return penaltyReputation;
    }

    public void setPenaltyReputation(int penaltyReputation) {
        this.penaltyReputation = penaltyReputation;
    }

    public String getNextQuestId() {
        return nextQuestId;
    }

    public void setNextQuestId(String nextQuestId) {
        this.nextQuestId = nextQuestId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
