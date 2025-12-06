package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import factory.EventFactory;
import manager.DataManager;
import model.EventBus;
import model.GameState;
import model.entity.Event;
import model.enums.EventStatus;
import model.enums.EventType;
import model.enums.BusEvent;

public class EventService {
    private List<Map<String, Object>> eventConfig;
    private Map<String, Event> activeEvents = new HashMap<>();
    private Map<String, Event> scheduledEvents = new HashMap<>();
    private List<Map<String, Object>> randomEventConfigs = new ArrayList<>();
    private EventBus eventBus;

    public EventService(DataManager dataManager, EventBus eventBus) {
        if (dataManager == null) {
            throw new IllegalArgumentException("dataManager는 필수입니다.");
        }
        this.eventConfig = dataManager.getEventConfig();
        this.eventBus = eventBus;

        // EventBus 구독 설정
        subscribeToEvents();
    }

    /**
     * EventBus 이벤트 구독
     */
    private void subscribeToEvents() {
        // DAY_CHANGED 이벤트 구독 - 날짜가 바뀔 때마다 이벤트 체크
        eventBus.subscribe(BusEvent.DAY_CHANGED, data -> {
            if (data instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> eventData = (Map<String, Object>) data;
                LocalDate currentDate = (LocalDate) eventData.get("currentDate");
                System.out.println("[EventService] DAY_CHANGED 수신: " + currentDate);
                checkEvents(currentDate);
                checkScheduledEvents(currentDate);
            }
        });
    }

    public void setEventConfig(List<Map<String, Object>> eventConfig) {
        this.eventConfig = eventConfig;
        if (this.eventConfig != null) {
            for (Map<String, Object> config : this.eventConfig) {
                String triggerType = (String) config.get("triggerType");

                if ("SCHEDULED".equals(triggerType)) {
                    // scheduledDate가 null이면 후속 이벤트이므로 패스 (나중에 동적으로 스케줄됨)
                    if (config.get("scheduledDate") != null) {
                        Event event = EventFactory.createScheduledEvent(config);
                        event.setStatus(EventStatus.ACTIVE);
                        this.scheduledEvents.put(event.getId(), event);
                    }
                }
                if ("RANDOM".equals(triggerType)) {
                    this.randomEventConfigs.add(config);
                }
            }
        }
    }

    public void checkEvents(LocalDate today) {
        for (Map<String, Object> config : randomEventConfigs) {
            String id = (String) config.get("id");
            if (activeEvents.containsKey(id)) {
                continue;
            }

            double probability = 0.0;
            if (config.containsKey("probability")) {
                probability = ((Number) config.get("probability")).doubleValue();
            }

            if (Math.random() < probability) {
                Event event = EventFactory.createRandomEvent(config, today);

                activeEvents.put(event.getId(), event);
                eventBus.emit(BusEvent.EVENT_TRIGGERED, event);
                System.out.println("[EventService] 랜덤 이벤트 발생: " + event.getTitle());

                // 후속 이벤트 스케줄링
                scheduleSubsequentEvents(event, today);

                break; // Trigger only one event per day
            }
        }
    }

    /**
     * 스케줄된 이벤트 체크 - 해당 날짜에 예정된 이벤트 트리거
     */
    public void checkScheduledEvents(LocalDate today) {
        List<String> triggeredIds = new ArrayList<>();

        for (Map.Entry<String, Event> entry : scheduledEvents.entrySet()) {
            Event event = entry.getValue();

            // 이미 활성화된 이벤트는 스킵
            if (activeEvents.containsKey(event.getId())) {
                continue;
            }

            // 날짜가 일치하면 트리거
            if (event.getTriggeredAt() != null && event.getTriggeredAt().equals(today)) {
                activeEvents.put(event.getId(), event);
                eventBus.emit(BusEvent.EVENT_TRIGGERED, event);
                triggeredIds.add(event.getId());
                System.out.println("[EventService] 스케줄 이벤트 발생: " + event.getTitle() + " (날짜: " + today + ")");

                // 후속 이벤트 스케줄링
                scheduleSubsequentEvents(event, today);
            }
        }

        // 트리거된 이벤트는 scheduledEvents에서 제거
        for (String id : triggeredIds) {
            scheduledEvents.remove(id);
        }
    }

    // 후속 이벤트를 스케줄하는 메소드
    private void scheduleSubsequentEvents(Event triggeredEvent, LocalDate triggerDate) {
        if (triggeredEvent.getSubsequentEventIds() == null || triggeredEvent.getSubsequentEventIds().isEmpty()) {
            return;
        }

        int dayOffset = 1; // 첫 번째 후속 이벤트는 1일 후

        for (String subsequentId : triggeredEvent.getSubsequentEventIds()) {
            // config에서 해당 이벤트 찾기
            Map<String, Object> subsequentConfig = findEventConfigById(subsequentId);
            if (subsequentConfig == null) {
                continue;
            }

            // 후속 이벤트 생성
            Event subsequentEvent = EventFactory.createScheduledEvent(subsequentConfig);
            LocalDate scheduledDate = triggerDate.plusDays(dayOffset);
            subsequentEvent.setTriggeredAt(scheduledDate);

            // scheduledEvents에 추가
            scheduledEvents.put(subsequentEvent.getId(), subsequentEvent);

            System.out.println("EventService: Scheduled subsequent event " + subsequentId + " for " + scheduledDate);

            dayOffset++; // 다음 후속 이벤트는 하루 더 뒤에
        }
    }

    // ID로 이벤트 config 찾기
    private Map<String, Object> findEventConfigById(String eventId) {
        if (eventConfig == null) {
            return null;
        }

        for (Map<String, Object> config : eventConfig) {
            if (eventId.equals(config.get("id"))) {
                return config;
            }
        }
        return null;
    }

    public Event triggerEvent(EventType type) {
        Event event = new Event();
        event.setType(type);
        event.setStatus(EventStatus.ACTIVE);
        // ... initialize other fields
        activeEvents.put(event.getId(), event);
        eventBus.emit(BusEvent.EVENT_TRIGGERED, event);
        return event;
    }

    public List<Event> getActiveEvents() {
        return activeEvents.values().stream()
                .filter(e -> e.getStatus() == EventStatus.ACTIVE)
                .collect(Collectors.toList());
    }
}
