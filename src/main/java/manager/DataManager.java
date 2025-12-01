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

        try {
            // 이벤트 설정 로드 (classpath에서 로드)
            String eventPath = getClass().getClassLoader().getResource("event_config.json").getPath();
            eventPath = java.net.URLDecoder.decode(eventPath, "UTF-8");
            this.eventConfig = JsonLoader.loadJSON(eventPath);

            if (this.eventConfig != null) {
                System.out.println("DataManager: Loaded " + this.eventConfig.size() + " events.");
            } else {
                System.err.println("DataManager: Failed to load event_config.json");
            }
        } catch (Exception e) {
            System.err.println("DataManager: Error loading event_config.json - " + e.getMessage());
            e.printStackTrace();
        }

        try {
            // 퀘스트 설정 로드 (classpath에서 로드)
            String questPath = getClass().getClassLoader().getResource("quest_config.json").getPath();
            questPath = java.net.URLDecoder.decode(questPath, "UTF-8");
            this.questConfig = JsonLoader.loadJSON(questPath);

            if (this.questConfig != null) {
                System.out.println("DataManager: Loaded " + this.questConfig.size() + " quests.");
            } else {
                System.err.println("DataManager: Failed to load quest_config.json");
            }
        } catch (Exception e) {
            System.err.println("DataManager: Error loading quest_config.json - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> getEventConfig() {
        return eventConfig;
    }

    public List<Map<String, Object>> getQuestConfig() {
        return questConfig;
    }
}
