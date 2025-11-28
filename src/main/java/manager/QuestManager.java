package manager;

import java.util.ArrayList;
import java.util.List;
import model.entity.Quest;
import model.enums.QuestStatus;
import model.GameState;
import model.EventBus;
import factory.QuestFactory;

public class QuestManager {
    private List<Quest> quests = new ArrayList<>();
    private GameState gameState;

    public QuestManager(GameState gameState) {
        this.gameState = gameState;
    }

    public void completeQuest(String id) {
        Quest quest = quests.stream()
                .filter(q -> q.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (!quest.isComplete())
            return;
        quest.setStatus(QuestStatus.COMPLETED);
        gameState.addReputation(quest.getRewardReputation());

        EventBus.getInstance().emit("QUEST_COMPLETED", quest);

        if (quest.hasNextQuest()) {
            String nextQuestId = quest.getNextQuestId();
            QuestFactory.createQuest(nextQuestId);
        }

    }

}
