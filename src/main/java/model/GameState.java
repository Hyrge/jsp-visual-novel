package model;

import java.util.Map;

public class GameState {
    private int reputation = 20;

    public GameState() {
    }

    public void addReputation(int value) {
        int before = this.reputation;
        this.reputation = Math.max(0, Math.min(100, reputation + value));
        EventBus.getInstance().emit("REPUTATION_CHANGED", Map.of(
                "before", before,
                "after", reputation,
                "delta", value));
    }

    public int getReputation() {
        return reputation;
    }

}
