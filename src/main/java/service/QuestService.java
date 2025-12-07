package service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import factory.QuestFactory;
import manager.DataManager;
import model.EventBus;
import model.GameState;
import model.entity.Quest;
import model.entity.QuestObjective;
import model.enums.QuestStatus;
import model.enums.BusEvent;
import util.SavePathManager;

/**
 * 퀘스트 관리 서비스
 * - 퀘스트 로드/저장
 * - 진행도 업데이트
 * - 완료 처리
 */
public class QuestService {
    private List<Quest> quests = new ArrayList<>();
    private GameState gameState;
    private EventBus eventBus;
    private String pid;

    public QuestService(GameState gameState, EventBus eventBus) {
        this.gameState = gameState;
        this.eventBus = eventBus;

        // EVENT_TRIGGERED 구독 - 이벤트 발생 시 관련 퀘스트 활성화
        subscribeToEvents();
    }

    /**
     * EventBus 이벤트 구독
     */
    private void subscribeToEvents() {
        eventBus.subscribe(BusEvent.EVENT_TRIGGERED, data -> {
            if (data instanceof model.entity.Event) {
                model.entity.Event event = (model.entity.Event) data;
                activateEventQuests(event.getId());
            }
        });
    }

    /**
     * 플레이어별 퀘스트 초기화
     */
    public void initQuests(String pid) {
        this.pid = pid;

        // 저장된 퀘스트 로드
        List<Quest> savedQuests = SavePathManager.loadQuests(pid);

        if (savedQuests != null && !savedQuests.isEmpty()) {
            this.quests = savedQuests;
            System.out.println("[QuestService] 저장된 퀘스트 로드: " + quests.size() + "개");
        } else {
            // 초기 퀘스트 로드 (AVAILABLE 상태인 것들)
            loadInitialQuests();
        }
    }

    /**
     * quest_config.json에서 초기 퀘스트 로드
     */
    private void loadInitialQuests() {
        DataManager dataManager = DataManager.getInstance();
        List<?> questConfigs = dataManager.getQuestConfig();

        if (questConfigs == null) {
            System.out.println("[QuestService] quest_config.json 로드 실패");
            return;
        }

        for (Object obj : questConfigs) {
            if (!(obj instanceof java.util.Map))
                continue;

            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> config = (java.util.Map<String, Object>) obj;
            String status = (String) config.get("status");
            String relatedEventId = (String) config.get("relatedEventId");

            // AVAILABLE 상태이고, 이벤트 연동이 없는 퀘스트만 초기 로드
            if ("AVAILABLE".equals(status) && relatedEventId == null) {
                String id = (String) config.get("id");
                Quest quest = QuestFactory.createQuest(id);
                if (quest != null) {
                    quests.add(quest);
                    System.out.println("[QuestService] 초기 퀘스트 로드: " + quest.getTitle());
                }
            }
        }
    }

    /**
     * 이벤트 발생 시 관련 퀘스트 활성화
     */
    public void activateEventQuests(String eventId) {
        DataManager dataManager = DataManager.getInstance();
        List<?> questConfigs = dataManager.getQuestConfig();

        if (questConfigs == null)
            return;

        for (Object obj : questConfigs) {
            if (!(obj instanceof java.util.Map))
                continue;

            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> config = (java.util.Map<String, Object>) obj;
            String relatedEventId = (String) config.get("relatedEventId");

            // 해당 이벤트와 연관된 퀘스트 활성화
            if (eventId.equals(relatedEventId)) {
                String id = (String) config.get("id");

                // 이미 있는지 확인
                if (quests.stream().anyMatch(q -> q.getId().equals(id))) {
                    continue;
                }

                Quest quest = QuestFactory.createQuest(id);
                if (quest != null) {
                    quest.setStatus(QuestStatus.AVAILABLE);
                    quests.add(quest);
                    eventBus.emit(BusEvent.QUEST_ADDED, quest);
                    System.out
                            .println("[QuestService] 이벤트 연동 퀘스트 활성화: " + quest.getTitle() + " (이벤트: " + eventId + ")");
                }
            }
        }

        saveQuests();
    }

    /**
     * Objective 완료 처리
     */
    public boolean completeObjective(String questId, int objectiveId) {
        Quest quest = findQuestById(questId);
        if (quest == null)
            return false;

        List<QuestObjective> objectives = quest.getObjectives();
        if (objectives == null)
            return false;

        for (QuestObjective obj : objectives) {
            if (obj.getId() == objectiveId && !obj.isCompleted()) {
                obj.setCompleted(true);

                // 진행도 업데이트 이벤트
                eventBus.emit(BusEvent.QUEST_PROGRESS_UPDATED, quest);
                System.out.println("[QuestService] Objective 완료: " + questId + " - " + obj.getDescription());

                // 퀘스트 상태 체크 및 업데이트
                updateQuestStatus(quest);
                saveQuests();
                return true;
            }
        }
        return false;
    }


    /**
     * 퀘스트 진행도 증가 (단일 목표용)
     */
    public void addQuestProgress(String questId, int amount) {
        Quest quest = findQuestById(questId);
        if (quest != null && quest.getStatus() == QuestStatus.IN_PROGRESS) {
            quest.addProgress(amount);

            // 완료 조건 체크
            if (quest.isComplete()) {
                updateQuestStatus(quest);
            } else {
                eventBus.emit(BusEvent.QUEST_PROGRESS_UPDATED, quest);
                saveQuests();
            }
        }
    }

    /**
     * 퀘스트 상태 업데이트 (모든 objectives 완료 시 COMPLETABLE로)
     */
    private void updateQuestStatus(Quest quest) {
        if (quest.getStatus() == QuestStatus.COMPLETED)
            return;

        if (quest.isComplete()) {
            quest.setStatus(QuestStatus.COMPLETABLE);
            System.out.println("[QuestService] 퀘스트 완료 가능: " + quest.getTitle());
        } else if (quest.getStatus() == QuestStatus.AVAILABLE) {
            quest.setStatus(QuestStatus.IN_PROGRESS);
        }

    }

    /**
     * 퀘스트 완료 처리 (보상 지급, 다음 퀘스트 활성화)
     */
    public void completeQuest(String id) {
        Quest quest = findQuestById(id);
        if (quest == null || quest.getStatus() == QuestStatus.COMPLETED) {
            return;
        }

        // 완료 조건 체크
        if (!quest.isComplete()) {
            System.out.println("[QuestService] 퀘스트 완료 조건 미충족: " + quest.getTitle());
            return;
        }

        quest.setStatus(QuestStatus.COMPLETED);

        // 보상 지급
        gameState.addReputation(quest.getRewardReputation());
        System.out.println("[QuestService] 퀘스트 완료: " + quest.getTitle() + " (평판 +" + quest.getRewardReputation() + ")");

        // 시간 스킵 (spentTime)
        if (quest.getSpentTime() > 0) {
            gameState.advanceTime(quest.getSpentTime());
            System.out.println("[QuestService] 시간 경과: " + quest.getSpentTime() + "분");
        }

        eventBus.emit(BusEvent.QUEST_COMPLETED, quest);

        // 다음 퀘스트 활성화
        if (quest.hasNextQuest()) {
            Quest nextQuest = QuestFactory.createQuest(quest.getNextQuestId());
            if (nextQuest != null) {
                nextQuest.setStatus(QuestStatus.AVAILABLE);
                quests.add(nextQuest);
                eventBus.emit(BusEvent.QUEST_ADDED, nextQuest);
                System.out.println("[QuestService] 다음 퀘스트 활성화: " + nextQuest.getTitle());
            }
        }

        saveQuests();
    }

    /**
     * 활성 퀘스트 목록 (AVAILABLE, IN_PROGRESS, COMPLETABLE)
     */
    public List<Quest> getActiveQuests() {
        return quests.stream()
                .filter(q -> q.getStatus() == QuestStatus.AVAILABLE
                        || q.getStatus() == QuestStatus.IN_PROGRESS
                        || q.getStatus() == QuestStatus.COMPLETABLE)
                .collect(Collectors.toList());
    }

    /**
     * 모든 퀘스트 목록
     */
    public List<Quest> getAllQuests() {
        return new ArrayList<>(quests);
    }

    /**
     * ID로 퀘스트 찾기
     */
    public Quest findQuestById(String id) {
        return quests.stream()
                .filter(q -> q.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 퀘스트 추가
     */
    public void addQuest(Quest quest) {
        if (quest != null && quests.stream().noneMatch(q -> q.getId().equals(quest.getId()))) {
            quests.add(quest);
            saveQuests();
        }
    }

    /**
     * 퀘스트 저장
     */
    public void saveQuests() {
        if (pid != null) {
            SavePathManager.saveQuests(pid, quests);
        }
    }
}
