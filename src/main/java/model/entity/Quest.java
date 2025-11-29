package model.entity;

import java.time.LocalDateTime;
import java.util.List;

import model.enums.QuestIssuer;
import model.enums.QuestStatus;

public class Quest {
    private String id;
    private QuestIssuer issuer; // SYSTEM, COMPANY
    private String title;
    private String description;
    private QuestStatus status; // AVAILABLE, IN_PROGRESS, COMPLETABLE, COMPLETED, FAILED
    private LocalDateTime deadline;
    // 단일 목표
    private int currentProgress;
    private int requiredProgress;

    // 서브 목표 (UI 표시용)
    private List<QuestObjective> objectives;

    // 평판 보상/페널티
    private int rewardReputation;
    private int penaltyReputation;

    // 예상 소요 시간 (분)
    private int spentTime;

    // 체이닝
    private String nextQuestId;

    // 관련 이벤트 ID (있다면)
    private String relatedEventId;

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

    public List<QuestObjective> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<QuestObjective> objectives) {
        this.objectives = objectives;
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

    public int getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(int spentTime) {
        this.spentTime = spentTime;
    }

    public String getNextQuestId() {
        return nextQuestId;
    }

    public void setNextQuestId(String nextQuestId) {
        this.nextQuestId = nextQuestId;
    }

    public String getRelatedEventId() {
        return relatedEventId;
    }

    public void setRelatedEventId(String relatedEventId) {
        this.relatedEventId = relatedEventId;
    }

}
