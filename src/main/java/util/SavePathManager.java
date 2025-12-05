package util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import model.EventBus;
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

    // GameController용 오버로드 (EventBus 없이 호출 가능)
    public static boolean createPlayerSaveFolder(String pid) {
        return createPlayerSaveFolder(pid, null);
    }

    public static boolean createPlayerSaveFolder(String pid, EventBus eventBus) {
        try {
            Path playerDir = Paths.get(basePath, BASE_SAVE_DIR, pid);

            // 이미 존재하면 삭제함
            if (Files.exists(playerDir)) {

            }

            Files.createDirectories(playerDir);
            Files.createDirectories(playerDir.resolve("images"));

            // 초기 JSON 파일 생성 (EventBus는 나중에 주입될 것이므로 임시로 생성)
            EventBus tempEventBus = (eventBus != null) ? eventBus : new EventBus();
            GameState initialGameState = new GameState(tempEventBus);
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
    public static GameState loadGameState(String pid, EventBus eventBus) {
        try {
            String filePath = getJsonFilePath(pid, "gamestate.json");
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Game State file 이 존재하지 않음, 새로 생성");
                return new GameState(eventBus);
            }
            GameState gameState = objectMapper.readValue(file, GameState.class);
            // JSON에서 로드한 GameState는 EventBus가 없으므로 리플렉션으로 주입
            injectEventBus(gameState, eventBus);
            return gameState;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Game State file 로드 실패, 새로 생성" + e.getMessage());
            return new GameState(eventBus);
        }
    }

    /**
     * 리플렉션을 사용하여 GameState에 EventBus 주입
     */
    private static void injectEventBus(GameState gameState, EventBus eventBus) {
        try {
            Field eventBusField = GameState.class.getDeclaredField("eventBus");
            eventBusField.setAccessible(true);
            eventBusField.set(gameState, eventBus);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("EventBus 주입 실패", e);
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
