package model;

import manager.DataManager;
import service.EventService;
import service.MessageService;
import service.PostService;
import service.QuestService;
import util.SavePathManager;

public class GameContext {
    private final String pid;
    private final EventBus eventBus;
    private final GameState gameState;
    private final EventService eventService;
    private final QuestService questService;
    private final MessageService messageService;
    private final PostService postService;
    private final DataManager dataManager;

    public GameContext(String pid) {
        this.pid = pid;
        this.eventBus = new EventBus();
        this.dataManager = DataManager.getInstance();
        this.gameState = SavePathManager.loadGameState(this.pid, eventBus);
        this.eventService = new EventService(dataManager, eventBus);
        this.questService = new QuestService(gameState, eventBus);
        this.messageService = new MessageService(eventBus);
        this.postService = new PostService(dataManager);
    }

    // Getters
    public String getPid() {
        return pid;
    }

    public EventBus getEventBus() {
        return eventBus;
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

}
