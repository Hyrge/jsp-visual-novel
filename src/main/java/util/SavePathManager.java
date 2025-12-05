package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 플레이어별 저장 폴더 관리
 * saves/{pid}/ - gamestate.json, events.json, messages.json, quests.json, images/
 */
public class SavePathManager {

    private static final String BASE_SAVE_DIR = "saves";

    public static String getPlayerSavePath(String pid) {
        return BASE_SAVE_DIR + File.separator + pid;
    }

    public static boolean createPlayerSaveFolder(String basePath, String pid) {
        try {
            Path playerDir = Paths.get(basePath, BASE_SAVE_DIR, pid);

            // 이미 존재하면 삭제함
            if (Files.exists(playerDir)) {
                
            }

            Files.createDirectories(playerDir);
            Files.createDirectories(playerDir.resolve("images"));

            // 초기 JSON 파일 생성
            String gameStateJson = "{\n" +
                "  \"currentDate\": \"2025-09-01\",\n" +
                "  \"currentTime\": \"09:00:00\",\n" +
                "  \"reputation\": 50,\n" +
                "  \"albumSales\": 0\n" +
                "}";
            Files.writeString(playerDir.resolve("gamestate.json"), gameStateJson);
            Files.writeString(playerDir.resolve("events.json"), "[]");
            Files.writeString(playerDir.resolve("messages.json"), "[]");
            Files.writeString(playerDir.resolve("quests.json"), "[]");

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getJsonFilePath(String basePath, String pid, String fileName) {
        return Paths.get(basePath, BASE_SAVE_DIR, pid, fileName).toString();
    }

    public static String getImagesPath(String basePath, String pid) {
        return Paths.get(basePath, BASE_SAVE_DIR, pid, "images").toString();
    }

    public static boolean existsPlayerSaveFolder(String basePath, String pid) {
        Path playerDir = Paths.get(basePath, BASE_SAVE_DIR, pid);
        return Files.exists(playerDir) && Files.isDirectory(playerDir);
    }
}
