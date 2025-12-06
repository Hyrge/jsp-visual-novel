package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import model.EventBus;
import model.GameState;
import model.enums.BusEvent;
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
        eventBus.emit(BusEvent.TIME_ADVANCED, Map.of(
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

        System.out.println("[TimeService] 다음 이벤트로 시간 점프");
        jumpToTime(nextEventTime, false);
    }

    /**
     * ⏩ 버튼: 다음 날로 스킵
     * 다음 날 09:00 이전(08:59까지)의 모든 이벤트를 순차적으로 처리
     */
    public void skipToNextDay() {
        LocalDate currentDay = gameState.getCurrentDate();
        LocalDate nextDay = currentDay.plusDays(1);
        LocalDateTime nextDayStart = LocalDateTime.of(nextDay, LocalTime.of(9, 0));

        System.out.println("[TimeService] 다음날로 스킵: " + currentDay + " → " + nextDay);

        // 다음 날 09:00 이전의 모든 이벤트를 순차적으로 처리
        int eventCount = 0;
        while (true) {
            LocalDateTime nextEventTime = gameState.getNextEventTime();

            // 다음 이벤트가 없거나, 다음 날 09:00 이후의 이벤트면 종료
            if (nextEventTime == null || !nextEventTime.isBefore(nextDayStart)) {
                break;
            }

            // 이벤트 처리
            skipToNextEvent();
            eventCount++;
        }

        System.out.println("[TimeService] " + eventCount + "개의 이벤트 순차 처리 완료");

        // 마지막으로 다음 날 09:00으로 점프
        if (!gameState.getCurrentDateTime().equals(nextDayStart)) {
            jumpToTime(nextDayStart, true);
        }
    }

    /**
     * 특정 시간으로 점프 (내부 헬퍼 메서드)
     */
    private void jumpToTime(LocalDateTime targetTime, boolean isDaySkip) {
        LocalDateTime before = gameState.getCurrentDateTime();
        LocalDate beforeDate = before.toLocalDate();

        // 시간 점프 + 해당 이벤트만 제거
        gameState.setCurrentDate(targetTime.toLocalDate());
        gameState.setCurrentTime(targetTime.toLocalTime());
        gameState.removeEventTime(targetTime);  // 해당 이벤트만 제거

        // 시간 경과 이벤트 발행
        long minutesSkipped = java.time.Duration.between(before, targetTime).toMinutes();
        Map<String, Object> eventData = new java.util.HashMap<>();
        eventData.put("minutes", minutesSkipped);
        eventData.put("before", before);
        eventData.put("after", targetTime);
        eventData.put("skipped", true);
        if (isDaySkip) {
            eventData.put("daySkip", true);
        }
        eventBus.emit(BusEvent.TIME_ADVANCED, eventData);

        // 날짜 변경 체크
        if (!beforeDate.equals(targetTime.toLocalDate())) {
            emitDayChanged(beforeDate, targetTime.toLocalDate());
        }

        // 날짜 스킵인 경우 일일 정산 이벤트
        // if (isDaySkip) {
        //     eventBus.emit(BusEvent.DAILY_SETTLEMENT, Map.of("date", targetTime.toLocalDate()));
        // }
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
        eventBus.emit(BusEvent.DAY_CHANGED, Map.of(
            "previousDate", beforeDate,
            "currentDate", afterDate,
            "dDay", gameState.getDDayText()
        ));
    }
}
