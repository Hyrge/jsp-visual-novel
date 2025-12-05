package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import model.GameState;
import model.entity.Quest;

/**
 * 플레이어별 저장 폴더 관리
 * saves/{pid}/ - gamestate.json, events.json, messages.json, quests.json, images/
 */
public class SavePathManager {
    private static String basePath = System.getProperty("user.dir");
    private static final String BASE_SAVE_DIR = "saves";
    private static final ObjectMapper objectMapper;
    
    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static boolean createPlayerSaveFolder(String pid) {
        try {
            Path playerDir = Paths.get(basePath, BASE_SAVE_DIR, pid);

            // 이미 존재하면 삭제함
            if (Files.exists(playerDir)) {
                
            }

            Files.createDirectories(playerDir);
            Files.createDirectories(playerDir.resolve("images"));

            // 초기 JSON 파일 생성
            GameState initialGameState = new GameState();
            initialGameState.setReputation(20);
            saveGameState(pid, initialGameState);
            
            Files.writeString(playerDir.resolve("events.json"), "[]");
            Files.writeString(playerDir.resolve("messages.json"), "[]");
            Files.writeString(playerDir.resolve("quests.json"), "[]");

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getJsonFilePath(String pid, String fileName) {
        return Paths.get(basePath, BASE_SAVE_DIR, pid, fileName).toString();
    }

    public static String getImagesPath(String pid) {
        return Paths.get(basePath, BASE_SAVE_DIR, pid, "images").toString();
    }

    public static boolean existsPlayerSaveFolder(String pid) {
        Path playerDir = Paths.get(basePath, BASE_SAVE_DIR, pid);
        return Files.exists(playerDir) && Files.isDirectory(playerDir);
    }

    // ========== JSON 저장/로드 메서드 ==========

    /**
     * GameState를 JSON 파일로 저장
     */
    public static boolean saveGameState(String pid, GameState gameState) {
        try {
            String filePath = getJsonFilePath(pid, "gamestate.json");
            objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(filePath), gameState);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * GameState를 JSON 파일에서 로드
     */
    public static GameState loadGameState(String pid) {
        try {
            String filePath = getJsonFilePath(pid, "gamestate.json");
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            return objectMapper.readValue(file, GameState.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Quest 리스트를 JSON 파일로 저장
     */
    public static boolean saveQuests(String pid, List<Quest> quests) {
        try {
            String filePath = getJsonFilePath(pid, "quests.json");
            objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(filePath), quests);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Quest 리스트를 JSON 파일에서 로드
     */
    public static List<Quest> loadQuests(String pid) {
        try {
            String filePath = getJsonFilePath(pid, "quests.json");
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            return objectMapper.readValue(file, new TypeReference<List<Quest>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Events 리스트를 JSON 파일로 저장
     */
    public static boolean saveEvents(String pid, List<?> events) {
        try {
            String filePath = getJsonFilePath(pid, "events.json");
            objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(filePath), events);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Events 리스트를 JSON 파일에서 로드
     */
    public static <T> List<T> loadEvents(String pid, Class<T> eventType) {
        try {
            String filePath = getJsonFilePath(pid, "events.json");
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            return objectMapper.readValue(file, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, eventType));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Messages 리스트를 JSON 파일로 저장
     */
    public static boolean saveMessages(String pid, List<?> messages) {
        try {
            String filePath = getJsonFilePath(pid, "messages.json");
            objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(filePath), messages);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Messages 리스트를 JSON 파일에서 로드
     */
    public static <T> List<T> loadMessages(String pid, Class<T> messageType) {
        try {
            String filePath = getJsonFilePath(pid, "messages.json");
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            return objectMapper.readValue(file, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, messageType));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
