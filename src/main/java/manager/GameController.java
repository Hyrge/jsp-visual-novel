package manager;
import util.SavePathManager;
import dao.PlayerDAO;


public class GameController {
    private static GameController instance;
    private PlayerDAO playerDAO;

    private GameController() {
        // DataManager 초기화
        DataManager.getInstance();
        playerDAO = new PlayerDAO();
        System.out.println("GameController: Initialization Complete.");
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    // saves/{pid} 폴더 생성 하고 DB에 플레이어 저장
    public void createPlayer(String pid) {
        if (playerDAO.exists(pid)) {
            if (!SavePathManager.existsPlayerSaveFolder(pid)) {
                SavePathManager.createPlayerSaveFolder(pid);
            }
            return;
        }
        // saves/{pid} 폴더 생성
        SavePathManager.createPlayerSaveFolder(pid);
        playerDAO.createPlayer(pid);
    }
}

