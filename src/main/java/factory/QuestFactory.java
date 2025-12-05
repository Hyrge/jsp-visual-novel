package factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import manager.DataManager;
import model.entity.Quest;
import model.entity.QuestObjective;
import model.enums.QuestIssuer;
import model.enums.QuestStatus;

public class QuestFactory {
    public static Quest createQuest(String id) {
        DataManager dataManager = DataManager.getInstance();
        if (dataManager.getQuestConfig() == null) {
			return null;
		}

        Map<String, Object> config = dataManager.getQuestConfig().stream()
                .filter(c -> id.equals(c.get("id")))
                .findFirst()
                .orElse(null);

        if (config == null) {
			return null;
		}

        Quest quest = new Quest();
        quest.setId((String) config.get("id"));

        // issuer (SYSTEM / COMPANY)
        if (config.containsKey("issuer") && config.get("issuer") != null) {
            quest.setIssuer(QuestIssuer.valueOf((String) config.get("issuer")));
        }

        // 제목 및 내용 (JSON에서는 content로 들어옴)
        quest.setTitle((String) config.get("title"));
        if (config.containsKey("content")) {
            quest.setDescription((String) config.get("content"));
        }

        // 상태 (기본값 AVAILABLE)
        if (config.containsKey("status") && config.get("status") != null) {
            quest.setStatus(QuestStatus.valueOf((String) config.get("status")));
        } else {
            quest.setStatus(QuestStatus.AVAILABLE);
        }

        // 진행도 관련
        if (config.containsKey("requiredProgress") && config.get("requiredProgress") != null) {
            quest.setRequiredProgress(((Number) config.get("requiredProgress")).intValue());
        }

        // 서브 목표(objectives)는 UI/로직에서 쓸 수 있게 변환
        if (config.containsKey("objectives") && config.get("objectives") instanceof List) {
            List<?> rawObjectives = (List<?>) config.get("objectives");
            List<QuestObjective> objectives = new ArrayList<>();

            for (Object obj : rawObjectives) {
                if (!(obj instanceof Map)) {
					continue;
				}
                Map<?, ?> o = (Map<?, ?>) obj;

                QuestObjective qo = new QuestObjective();
                if (o.containsKey("id") && o.get("id") instanceof Number) {
                    qo.setId(((Number) o.get("id")).intValue());
                }
                if (o.containsKey("description")) {
                    qo.setDescription((String) o.get("description"));
                }
                if (o.containsKey("visible") && o.get("visible") instanceof Boolean) {
                    qo.setVisible((Boolean) o.get("visible"));
                } else {
                    qo.setVisible(true);
                }

                objectives.add(qo);
            }

            quest.setObjectives(objectives);
        }

        // 평판 관련
        if (config.containsKey("rewardReputation") && config.get("rewardReputation") != null) {
            quest.setRewardReputation(((Number) config.get("rewardReputation")).intValue());
        }
        if (config.containsKey("penaltyReputation") && config.get("penaltyReputation") != null) {
            quest.setPenaltyReputation(((Number) config.get("penaltyReputation")).intValue());
        }

        // 퀘스트 완료 후 소요 시간
        if (config.containsKey("spentTime") && config.get("spentTime") != null) {
            quest.setSpentTime(((Number) config.get("spentTime")).intValue());
        }

        // 체이닝 및 관련 이벤트
        if (config.containsKey("nextQuestId")) {
            quest.setNextQuestId((String) config.get("nextQuestId"));
        }
        if (config.containsKey("relatedEventId")) {
            quest.setRelatedEventId((String) config.get("relatedEventId"));
        }

        return quest;
    }
}
