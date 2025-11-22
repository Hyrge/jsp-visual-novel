package service;

import dto.Scene;
import dto.Choice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 씬 데이터를 관리하는 서비스 클래스
 * story.json 파일을 읽어서 씬 정보를 제공
 */
public class SceneManager {
    private Map<Integer, Scene> sceneMap;

    /**
     * story.json 파일을 읽어서 씬 데이터 로드
     */
    public SceneManager(String jsonPath) {
        sceneMap = new HashMap<>();
        loadScenes(jsonPath);
    }

    /**
     * JSON 파일에서 씬 데이터 로드
     */
    private void loadScenes(String jsonPath) {
        try {
            Gson gson = new Gson();

            // JSON 파일 읽기 (클래스패스에서)
            Reader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(jsonPath),
                "UTF-8"
            );

            // JSON을 Map으로 파싱
            Type type = new TypeToken<Map<String, List<Scene>>>(){}.getType();
            Map<String, List<Scene>> data = gson.fromJson(reader, type);

            // Scene 리스트를 Map으로 변환 (id를 키로)
            List<Scene> scenes = data.get("scenes");
            if (scenes != null) {
                for (Scene scene : scenes) {
                    sceneMap.put(scene.getId(), scene);
                }
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("씬 데이터 로드 실패: " + e.getMessage());
        }
    }

    /**
     * 씬 ID로 씬 가져오기
     */
    public Scene getScene(int sceneId) {
        return sceneMap.get(sceneId);
    }

    /**
     * 모든 씬 개수 반환
     */
    public int getSceneCount() {
        return sceneMap.size();
    }

    /**
     * 씬이 존재하는지 확인
     */
    public boolean hasScene(int sceneId) {
        return sceneMap.containsKey(sceneId);
    }
}
