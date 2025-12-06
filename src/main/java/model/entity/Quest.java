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
     * 모든 objectives가 완료되었는지 확인
     */
    public boolean isComplete() {
        if (objectives == null || objectives.isEmpty()) {
            return false;
        }
        return objectives.stream().allMatch(QuestObjective::isCompleted);
    }

    /**
     * 완료된 objectives 수 반환
     */
    public int getCompletedCount() {
        if (objectives == null)
            return 0;
        return (int) objectives.stream().filter(QuestObjective::isCompleted).count();
    }

    /**
     * 전체 objectives 수 반환
     */
    public int getTotalCount() {
        return objectives != null ? objectives.size() : 0;
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
