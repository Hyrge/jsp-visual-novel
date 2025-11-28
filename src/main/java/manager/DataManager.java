package manager;

import util.JsonLoader;
import java.io.File;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static DataManager instance;
    private List<Map<String, Object>> eventConfig;
    // 추후 퀘스트 설정 등 추가

    private DataManager() {
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void loadAll(String rootPath) {
        // 경로 보정 (끝에 구분자 확인)
        if (!rootPath.endsWith(File.separator)) {
            rootPath += File.separator;
        }
        String dataPath = rootPath + "data" + File.separator;

        System.out.println("DataManager: Loading data from " + dataPath);

        // 이벤트 설정 로드
        this.eventConfig = JsonLoader.loadJSON(dataPath + "event_config.json");

        if (this.eventConfig != null) {
            System.out.println("DataManager: Loaded " + this.eventConfig.size() + " events.");
        } else {
            System.err.println("DataManager: Failed to load event_config.json");
        }
    }

    public List<Map<String, Object>> getEventConfig() {
        return eventConfig;
    }
}
