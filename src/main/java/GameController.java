

import manager.DataManager;
import java.io.File;

public class GameController {
    private static GameController instance;
    private boolean initialized = false;

    private GameController() {
    }

    public static synchronized GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public void init(String realPath) {
        if (initialized)
            return;

        System.out.println("GameController: Initializing...");

        // DataManager 초기화
        DataManager.getInstance().loadAll(realPath);

        initialized = true;
        System.out.println("GameController: Initialization Complete.");
    }
}
