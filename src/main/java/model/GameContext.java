package model;

import manager.DataManager;
import service.EventService;
import service.MessageService;
import service.PostService;
import service.QuestService;

public class GameContext {
    private String pid;
    private final GameState gameState;
    private final EventService eventService;
    private final QuestService questService;
    private final MessageService messageService;
    private final PostService postService;
    private final DataManager dataManager;

    public GameContext() {
        this.pid = null;
        this.dataManager = DataManager.getInstance();
        this.gameState = new GameState();
        this.eventService = new EventService(dataManager);
        this.questService = new QuestService(gameState);
        this.messageService = new MessageService();
        this.postService = new PostService(dataManager);
    }

    // Getters
    public String getPid() {
        return pid;
    }

    public GameState getGameState() {
        return gameState;
    }

    public EventService getEventService() {
        return eventService;
    }

    public QuestService getQuestService() {
        return questService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public PostService getPostService() {
        return postService;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
