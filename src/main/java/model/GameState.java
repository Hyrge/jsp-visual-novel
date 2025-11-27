package model;

import java.util.Map;

public class GameState {
    private int reputation = 50;
    private EventBus eventBus;

    public GameState(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void addReputation(int value) {
        int before = this.reputation;
        this.reputation = Math.max(0, Math.min(100, reputation + value));
        if (eventBus != null) {
            eventBus.emit("REPUTATION_CHANGED", Map.of(
                    "before", before,
                    "after", reputation,
                    "delta", value));
        }
    }

    public int getReputation() {
        return reputation;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
