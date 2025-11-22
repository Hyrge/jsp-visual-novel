package dto;

import java.sql.Timestamp;

/**
 * 게임 저장 데이터를 담는 모델 클래스
 */
public class SaveData {
    private int saveId;
    private int userId;
    private int currentScene;          // 현재 씬 번호
    private String choiceHistory;      // 선택 이력 (JSON 형태로 저장)
    private int affectionMina;         // 미나 호감도
    private int affectionKangwoo;      // 강우 호감도
    private String gameRoute;          // 진행 중인 루트 (mina, kangwoo, normal 등)
    private Timestamp lastSaved;       // 마지막 저장 시간

    // 기본 생성자
    public SaveData() {}

    // 전체 필드 생성자
    public SaveData(int saveId, int userId, int currentScene, String choiceHistory,
                    int affectionMina, int affectionKangwoo, String gameRoute, Timestamp lastSaved) {
        this.saveId = saveId;
        this.userId = userId;
        this.currentScene = currentScene;
        this.choiceHistory = choiceHistory;
        this.affectionMina = affectionMina;
        this.affectionKangwoo = affectionKangwoo;
        this.gameRoute = gameRoute;
        this.lastSaved = lastSaved;
    }

    // 새 게임 시작용 생성자
    public SaveData(int userId) {
        this.userId = userId;
        this.currentScene = 1;
        this.choiceHistory = "[]";
        this.affectionMina = 0;
        this.affectionKangwoo = 0;
        this.gameRoute = "normal";
    }

    // Getter & Setter
    public int getSaveId() {
        return saveId;
    }

    public void setSaveId(int saveId) {
        this.saveId = saveId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(int currentScene) {
        this.currentScene = currentScene;
    }

    public String getChoiceHistory() {
        return choiceHistory;
    }

    public void setChoiceHistory(String choiceHistory) {
        this.choiceHistory = choiceHistory;
    }

    public int getAffectionMina() {
        return affectionMina;
    }

    public void setAffectionMina(int affectionMina) {
        this.affectionMina = affectionMina;
    }

    public int getAffectionKangwoo() {
        return affectionKangwoo;
    }

    public void setAffectionKangwoo(int affectionKangwoo) {
        this.affectionKangwoo = affectionKangwoo;
    }

    public String getGameRoute() {
        return gameRoute;
    }

    public void setGameRoute(String gameRoute) {
        this.gameRoute = gameRoute;
    }

    public Timestamp getLastSaved() {
        return lastSaved;
    }

    public void setLastSaved(Timestamp lastSaved) {
        this.lastSaved = lastSaved;
    }

    @Override
    public String toString() {
        return "SaveData{" +
                "saveId=" + saveId +
                ", userId=" + userId +
                ", currentScene=" + currentScene +
                ", affectionMina=" + affectionMina +
                ", affectionKangwoo=" + affectionKangwoo +
                ", gameRoute='" + gameRoute + '\'' +
                ", lastSaved=" + lastSaved +
                '}';
    }
}
