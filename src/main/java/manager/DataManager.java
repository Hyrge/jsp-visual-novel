package manager;

import java.util.List;
import java.util.Map;

import util.JsonLoader;

public class DataManager {
    private static DataManager instance;
    private List<Map<String, Object>> eventConfig;
    private List<Map<String, Object>> questConfig;

    private DataManager() {
        loadAll();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void loadAll() {
        System.out.println("DataManager: Loading data");

        // 이벤트 설정 로드
        this.eventConfig = JsonLoader.loadJSON("../resources/event_config.json");

        if (this.eventConfig != null) {
            System.out.println("DataManager: Loaded " + this.eventConfig.size() + " events.");
        } else {
            System.err.println("DataManager: Failed to load event_config.json");
        }

        // 퀘스트 설정 로드
        this.questConfig = JsonLoader.loadJSON("../resources/quest_config.json");

        if (this.questConfig != null) {
            System.out.println("DataManager: Loaded " + this.questConfig.size() + " quests.");
        } else {
            System.err.println("DataManager: Failed to load quest_config.json");
        }
    }

    public List<Map<String, Object>> getEventConfig() {
        return eventConfig;
    }

    public List<Map<String, Object>> getQuestConfig() {
        return questConfig;
    }
}
