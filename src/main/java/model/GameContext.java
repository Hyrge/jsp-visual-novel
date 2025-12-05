package model;

import manager.DataManager;
import service.EventService;
import service.MessageService;
import service.PostService;
import service.QuestService;
import service.TimeService;
import util.SavePathManager;

public class GameContext {
    private final String pid;
    private final EventBus eventBus;
    private final GameState gameState;
    private final EventService eventService;
    private final QuestService questService;
    private final MessageService messageService;
    private final PostService postService;
    private final TimeService timeService;
    private final DataManager dataManager;

    public GameContext() {
        this.pid = null;
        this.eventBus = null;
        this.dataManager = null;
        this.gameState = null;
        this.eventService = null;
        this.questService = null;
        this.messageService = null;
        this.postService = null;
        this.timeService = null;
    }

    public GameContext(String pid) {
        this.pid = pid;
        this.eventBus = new EventBus();
        this.dataManager = DataManager.getInstance();
        this.gameState = SavePathManager.loadGameState(this.pid, eventBus);
        this.eventService = new EventService(dataManager, eventBus);
        this.questService = new QuestService(gameState, eventBus);
        this.messageService = new MessageService(eventBus);
        this.postService = new PostService(dataManager);
        this.timeService = new TimeService(gameState, eventBus);
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

    public TimeService getTimeService() {
        return timeService;
    }

}
