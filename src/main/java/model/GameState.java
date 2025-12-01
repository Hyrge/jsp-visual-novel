package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class GameState {
    private int reputation = 20;

    // 게임 내 현재 날짜 (초기값: 2025-09-01)
    private LocalDate currentDate = LocalDate.of(2025, 9, 1);

    // 게임 내 현재 시간 (예: 오전 9시 시작)
    private LocalTime currentTime = LocalTime.of(9, 0);

    // 게임의 목표 날짜 (고정값: 2025-12-03)
    private LocalDate targetDate = LocalDate.of(2025, 12, 3);

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

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public LocalTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(LocalTime currentTime) {
        this.currentTime = currentTime;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
}
