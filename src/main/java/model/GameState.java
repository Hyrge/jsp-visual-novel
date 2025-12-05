package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.PriorityQueue;
import model.enums.BusEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dto.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameState {
    @JsonIgnore
    private transient EventBus eventBus;
    private int reputation = 20;
    private User user;

    // 게임 내 현재 날짜 (초기값: 2025-09-01)
    private LocalDate currentDate = LocalDate.of(2025, 9, 1);

    // 게임 내 현재 시간 (예: 오전 9시 시작)
    private LocalTime currentTime = LocalTime.of(9, 0);

    // 게임의 목표 날짜 (고정값: 2025-12-03)
    private LocalDate targetDate = LocalDate.of(2025, 12, 3);

    // 이벤트 시간 큐 (시간순 자동 정렬)
    private PriorityQueue<LocalDateTime> eventTimes = new PriorityQueue<>();

    // Jackson 역직렬화용 기본 생성자
    public GameState() {
    }

    public GameState(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void addReputation(int value) {
        int before = this.reputation;
        this.reputation = Math.max(0, Math.min(100, reputation + value));
        eventBus.emit(BusEvent.REPUTATION_CHANGED, Map.of(
                "before", before,
                "after", reputation,
                "delta", value));
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
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
        LocalDateTime current = getCurrentDateTime();
        LocalDateTime target = current.plusMinutes(minutes);

        // 시간을 이동하면서 지나간 이벤트 시간들을 큐에서 제거
        while (!eventTimes.isEmpty() && !eventTimes.peek().isAfter(target)) {
            eventTimes.poll(); // 지나간 이벤트는 큐에서 제거
        }

        // 시간 이동
        this.currentDate = target.toLocalDate();
        this.currentTime = target.toLocalTime();
    }

    /**
     * 이벤트 시간을 큐에 추가
     * @param time 이벤트가 발생할 시간
     */
    public void addEventTime(LocalDateTime time) {
        eventTimes.add(time);
        System.out.println("[GameState] 이벤트 시간 추가: " + time + " (총 " + eventTimes.size() + "개)");
    }

    /**
     * 다음 이벤트 시간 조회 (큐에서 peek)
     * @return 다음 이벤트 시간, 없으면 null
     */
    public LocalDateTime getNextEventTime() {
        return eventTimes.peek();
    }

    /**
     * 다음 이벤트가 있는지 확인 (▶ 버튼 활성화 여부)
     * @return 다음 이벤트가 있으면 true
     */
    public boolean hasNextEvent() {
        boolean hasEvent = !eventTimes.isEmpty();
        System.out.println("[GameState] hasNextEvent 체크: " + hasEvent + " (큐 크기: " + eventTimes.size() + ")");
        if (hasEvent) {
            System.out.println("[GameState] 다음 이벤트 시간: " + eventTimes.peek());
        }
        return hasEvent;
    }

    /**
     * 다음 이벤트 시간으로 점프 (시계만 이동)
     */
    public void jumpToNextEvent() {
        LocalDateTime next = eventTimes.poll();
        if (next != null) {
            this.currentDate = next.toLocalDate();
            this.currentTime = next.toLocalTime();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
