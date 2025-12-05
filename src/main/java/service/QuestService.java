package service;

import java.util.ArrayList;
import java.util.List;

import factory.QuestFactory;
import model.EventBus;
import model.GameState;
import model.entity.Quest;
import model.enums.QuestStatus;

public class QuestService {
    private List<Quest> quests = new ArrayList<>();
    private GameState gameState;
    private EventBus eventBus;

    public QuestService(GameState gameState, EventBus eventBus) {
        this.gameState = gameState;
        this.eventBus = eventBus;
    }

    public void completeQuest(String id) {
        Quest quest = quests.stream()
                .filter(q -> q.getId().equals(id))
                .findFirst()
                .orElse(null);

        if ((quest == null) || (quest.getStatus() == QuestStatus.COMPLETED)) {
			return;
		}

        // TODO: Check if requirements are met (currentProgress >= requiredProgress)
        // For now, we assume calling completeQuest forces completion or checks are done
        // elsewhere.

        quest.setStatus(QuestStatus.COMPLETED);
        gameState.addReputation(quest.getRewardReputation());

        eventBus.emit("QUEST_COMPLETED", quest);

        if (quest.hasNextQuest()) {
            String nextQuestId = quest.getNextQuestId();
            Quest nextQuest = QuestFactory.createQuest(nextQuestId);
            if (nextQuest != null) {
                quests.add(nextQuest);
                eventBus.emit("QUEST_ADDED", nextQuest);
            }
        }
    }

    public void addQuest(Quest quest) {
        quests.add(quest);
    }

}

