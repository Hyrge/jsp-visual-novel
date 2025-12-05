package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import model.EventBus;
import model.GameState;

/**
 * 게임 시간 진행 관리 서비스
 * - 시간만 진행, 데이터는 시간 필터링으로 조회
 */
public class TimeService {
    private final GameState gameState;
    private final EventBus eventBus;

    public TimeService(GameState gameState, EventBus eventBus) {
        this.gameState = gameState;
        this.eventBus = eventBus;
    }

    /**
     * 게임 시간을 분 단위로 진행
     * @param minutes 진행할 분
     */
    public void advanceTime(int minutes) {
        LocalDateTime before = gameState.getCurrentDateTime();
        gameState.advanceTime(minutes);
        LocalDateTime after = gameState.getCurrentDateTime();

        System.out.println("[TimeService] 시간 진행: " + before + " → " + after + " (+" + minutes + "분)");

        // 시간 경과 이벤트 발행
        eventBus.emit("TIME_ADVANCED", Map.of(
            "minutes", minutes,
            "before", before,
            "after", after
        ));

        // 날짜가 바뀌었는지 체크
        if (!before.toLocalDate().equals(after.toLocalDate())) {
            emitDayChanged(before.toLocalDate(), after.toLocalDate());
        }
    }

    /**
     * ▶ 버튼: 다음 이벤트 시간으로 점프
     */
    public void skipToNextEvent() {
        LocalDateTime nextEventTime = gameState.getNextEventTime();

        if (nextEventTime == null) {
            System.out.println("[TimeService] 다음 이벤트가 없습니다.");
            return;
        }

        LocalDateTime before = gameState.getCurrentDateTime();
        LocalDate beforeDate = before.toLocalDate();

        // 시간 점프
        gameState.setCurrentDate(nextEventTime.toLocalDate());
        gameState.setCurrentTime(nextEventTime.toLocalTime());

        System.out.println("[TimeService] 시간 점프: " + before + " → " + nextEventTime);

        // 시간 경과 이벤트 발행
        long minutesSkipped = java.time.Duration.between(before, nextEventTime).toMinutes();
        eventBus.emit("TIME_ADVANCED", Map.of(
            "minutes", minutesSkipped,
            "before", before,
            "after", nextEventTime,
            "skipped", true
        ));

        // 날짜 변경 체크
        if (!beforeDate.equals(nextEventTime.toLocalDate())) {
            emitDayChanged(beforeDate, nextEventTime.toLocalDate());
        }
    }

    /**
     * ⏩ 버튼: 다음 날로 스킵
     */
    public void skipToNextDay() {
        LocalDateTime before = gameState.getCurrentDateTime();
        LocalDate beforeDate = before.toLocalDate();
        LocalDate nextDay = beforeDate.plusDays(1);
        LocalDateTime nextDayStart = LocalDateTime.of(nextDay, LocalTime.of(9, 0)); // 다음날 오전 9시

        // 시간 점프
        gameState.setCurrentDate(nextDay);
        gameState.setCurrentTime(LocalTime.of(9, 0));

        System.out.println("[TimeService] 날짜 스킵: " + beforeDate + " → " + nextDay);

        // 시간 경과 이벤트 발행
        long minutesSkipped = java.time.Duration.between(before, nextDayStart).toMinutes();
        eventBus.emit("TIME_ADVANCED", Map.of(
            "minutes", minutesSkipped,
            "before", before,
            "after", nextDayStart,
            "skipped", true,
            "daySkip", true
        ));

        // 날짜 변경 이벤트
        emitDayChanged(beforeDate, nextDay);

        // 일일 정산 이벤트
        eventBus.emit("DAILY_SETTLEMENT", Map.of(
            "date", nextDay
        ));
    }

    /**
     * 다음 이벤트까지 남은 시간(분) 반환
     */
    public long getMinutesToNextEvent() {
        LocalDateTime nextEventTime = gameState.getNextEventTime();

        if (nextEventTime == null) {
            return -1;
        }

        return java.time.Duration.between(gameState.getCurrentDateTime(), nextEventTime).toMinutes();
    }

    /**
     * 다음 이벤트 시간 조회
     */
    public LocalDateTime getNextEventTime() {
        return gameState.getNextEventTime();
    }

    /**
     * 다음 이벤트가 있는지 확인 (▶ 버튼 활성화 여부)
     */
    public boolean hasNextEvent() {
        return gameState.hasNextEvent();
    }

    /**
     * 날짜 변경 이벤트 발행 (private helper)
     */
    private void emitDayChanged(LocalDate beforeDate, LocalDate afterDate) {
        eventBus.emit("DAY_CHANGED", Map.of(
            "previousDate", beforeDate,
            "currentDate", afterDate,
            "dDay", gameState.getDDayText()
        ));
    }
}
