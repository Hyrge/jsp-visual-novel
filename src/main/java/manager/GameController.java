package manager;

public class GameController {
    private static GameController instance;

    private GameController() {
        // DataManager 초기화
        DataManager.getInstance();
        System.out.println("GameController: Initialization Complete.");
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

}
