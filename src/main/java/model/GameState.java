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

    /**
     * 앨범 발매일까지 남은 일수 계산
     * @return D-Day 텍스트 (예: "D-93", "D-Day", "D+7")
     */
    public String getDDayText() {
        long daysUntilRelease = java.time.temporal.ChronoUnit.DAYS.between(currentDate, targetDate);
        if (daysUntilRelease > 0) {
            return "D-" + daysUntilRelease;
        } else if (daysUntilRelease == 0) {
            return "D-Day";
        } else {
            return "D+" + Math.abs(daysUntilRelease);
        }
    }

    /**
     * 앨범 발매일까지 남은 일수 반환
     * @return 남은 일수 (음수일 경우 발매 이후)
     */
    public long getDaysUntilRelease() {
        return java.time.temporal.ChronoUnit.DAYS.between(currentDate, targetDate);
    }

    /**
     * 현재 게임 날짜와 시간을 LocalDateTime으로 반환
     * @return 현재 게임 날짜시간
     */
    public java.time.LocalDateTime getCurrentDateTime() {
        return java.time.LocalDateTime.of(currentDate, currentTime);
    }

    /**
     * 게임 시간을 분 단위로 진행
     * @param minutes 진행할 분
     */
    public void advanceTime(int minutes) {
        java.time.LocalDateTime current = getCurrentDateTime();
        java.time.LocalDateTime advanced = current.plusMinutes(minutes);
        this.currentDate = advanced.toLocalDate();
        this.currentTime = advanced.toLocalTime();
    }
}
