package model;

import manager.DataManager;
import manager.EventManager;
import manager.MessageManager;
import manager.QuestManager;

public class GameContext {
    private String pid;
    private GameState gameState;
    private EventManager eventManager;
    private QuestManager questManager;
    private MessageManager messageManager;

    public GameContext() {
        // No-arg constructor for jsp:useBean
    }

    public GameContext(String pid, DataManager dataManager) {
        init(pid, dataManager);
    }

    public void init(String pid, DataManager dataManager) {
        if (this.pid != null) {
			return; // 이미 초기화됨
		}

        this.pid = pid;
        // 1. GameState 생성 (EventBus는 싱글톤 사용)
        this.gameState = new GameState();

        // 2. Manager 생성
        this.eventManager = new EventManager(gameState);
        this.questManager = new QuestManager(gameState);
        this.messageManager = new MessageManager(gameState);

        // 3. 초기 데이터 로드 (DataManager의 템플릿 활용)
        initializeFromData(dataManager);
    }

    private void initializeFromData(DataManager dataManager) {
        // 이벤트 설정 로드
        if (dataManager.getEventConfig() != null) {
            eventManager.setEventConfig(dataManager.getEventConfig());
        }

        // 초기 환영 메시지
        model.entity.Message welcomeMsg = new model.entity.Message();
        welcomeMsg.setId(java.util.UUID.randomUUID().toString());
        welcomeMsg.setSender(model.enums.SenderType.SYSTEM);
        welcomeMsg.setTitle("환영합니다!");
        welcomeMsg.setContent("더꾸 연습생 육성 시뮬레이션에 오신 것을 환영합니다.\n열심히 활동하여 최고의 스타를 만들어보세요!");
        welcomeMsg.setRead(false);
        welcomeMsg.setCreatedAt(java.time.LocalDateTime.now());
        messageManager.sendMessage(welcomeMsg);
    }

    // Getters
    public String getPid() {
        return pid;
    }

    public GameState getGameState() {
        return gameState;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }
}
