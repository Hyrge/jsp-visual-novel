package model.entity;

import java.util.List;

import model.enums.QuestIssuer;
import model.enums.QuestStatus;

/**
 * 퀘스트 엔티티
 * quest_config.json 기반
 */
public class Quest {
    private String id;
    private QuestIssuer issuer; // SYSTEM, COMPANY
    private String title;
    private String description;
    private QuestStatus status; // PENDING, AVAILABLE, IN_PROGRESS, COMPLETABLE, COMPLETED, FAILED

    // 진행도 (objectives가 없을 때 사용)
    private int currentProgress;
    private int requiredProgress;

    // 서브 목표 (objectives만 사용, 단일 목표 없음)
    private List<QuestObjective> objectives;

    // 평판 보상/페널티
    private int rewardReputation;
    private int penaltyReputation;

    // 퀘스트 완료 후 스킵할 시간 (분)
    private int spentTime;

    // 체이닝
    private String nextQuestId;

    // 관련 이벤트 ID (있다면)
    private String relatedEventId;

    /**
     * 완료 조건 확인
     * - objectives가 있으면: 모든 objectives 완료 시 true
     * - objectives가 없으면: currentProgress >= requiredProgress 시 true
     */
    public boolean isComplete() {
        if (objectives != null && !objectives.isEmpty()) {
            return objectives.stream().allMatch(QuestObjective::isCompleted);
        }
        return currentProgress >= requiredProgress;
    }

    /**
     * 완료된 수치 반환
     */
    public int getCompletedCount() {
        if (objectives != null && !objectives.isEmpty()) {
            return (int) objectives.stream().filter(QuestObjective::isCompleted).count();
        }
        return currentProgress;
    }

    /**
     * 전체 목표 수치 반환
     */
    public int getTotalCount() {
        if (objectives != null && !objectives.isEmpty()) {
            return objectives.size();
        }
        return requiredProgress;
    }

    /**
     * 진행도 증가 (단일 목표 퀘스트용)
     */
    public void addProgress(int amount) {
        this.currentProgress += amount;
        if (this.currentProgress > this.requiredProgress) {
            this.currentProgress = this.requiredProgress;
        }
    }

    public boolean hasNextQuest() {
        return nextQuestId != null && !nextQuestId.isEmpty();
    }

    // Getters and Setters
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
