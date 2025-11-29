package factory;

import manager.DataManager;
import model.entity.Quest;
import model.enums.QuestIssuer;
import model.enums.QuestOrigin;
import model.enums.QuestStatus;
import java.util.Map;

public class QuestFactory {
    public static Quest createQuest(String id) {
        DataManager dataManager = DataManager.getInstance();
        if (dataManager.getQuestConfig() == null)
            return null;

        Map<String, Object> config = dataManager.getQuestConfig().stream()
                .filter(c -> id.equals(c.get("id")))
                .findFirst()
                .orElse(null);

        if (config == null)
            return null;

        Quest quest = new Quest();
        quest.setId((String) config.get("id"));

        if (config.containsKey("origin")) {
            quest.setOrigin(QuestOrigin.valueOf((String) config.get("origin")));
        }
        if (config.containsKey("issuer")) {
            quest.setIssuer(QuestIssuer.valueOf((String) config.get("issuer")));
        }
        quest.setTitle((String) config.get("title"));
        quest.setDescription((String) config.get("description"));

        if (config.containsKey("required")) {
            quest.setRequired((Boolean) config.get("required"));
        }

        quest.setStatus(QuestStatus.AVAILABLE);

        if (config.containsKey("requiredProgress")) {
            quest.setRequiredProgress(((Number) config.get("requiredProgress")).intValue());
        }
        if (config.containsKey("rewardReputation")) {
            quest.setRewardReputation(((Number) config.get("rewardReputation")).intValue());
        }
        if (config.containsKey("penaltyReputation")) {
            quest.setPenaltyReputation(((Number) config.get("penaltyReputation")).intValue());
        }
        if (config.containsKey("nextQuestId")) {
            quest.setNextQuestId((String) config.get("nextQuestId"));
        }

        return quest;
    }
}
