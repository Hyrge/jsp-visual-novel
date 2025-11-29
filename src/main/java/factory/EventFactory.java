package factory;

import model.entity.Event;
import model.enums.EventCategory;
import model.enums.EventStatus;
import model.enums.EventType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventFactory {

    public static Event createScheduledEvent(Map<String, Object> eventConfig) {
        Event event = createCommonEvent(eventConfig);

        if (eventConfig.containsKey("scheduledDate")) {
            event.setTriggeredAt(LocalDate.parse((String) eventConfig.get("scheduledDate")));
        }
        if (eventConfig.containsKey("scheduledTime")) {
            event.setTriggeredTime(LocalTime.parse((String) eventConfig.get("scheduledTime")));
        }

        return event;
    }

    public static Event createRandomEvent(Map<String, Object> eventConfig, LocalDate today) {
        Event event = createCommonEvent(eventConfig);
        event.setTriggeredAt(today);

        if (eventConfig.containsKey("scheduledTime")) {
            event.setTriggeredTime(LocalTime.parse((String) eventConfig.get("scheduledTime")));
        } else {
            event.setTriggeredTime(LocalTime.of(12, 0));
        }

        return event;
    }

    private static Event createCommonEvent(Map<String, Object> eventConfig) {
        Event event = new Event();
        if (eventConfig.containsKey("id")) {
            event.setId((String) eventConfig.get("id"));
        }
        if (eventConfig.containsKey("type")) {
            event.setType(EventType.valueOf((String) eventConfig.get("type")));
        }
        if (eventConfig.containsKey("category")) {
            event.setCategory(EventCategory.valueOf((String) eventConfig.get("category")));
        }
        if (eventConfig.containsKey("title")) {
            event.setTitle((String) eventConfig.get("title"));
        }
        if (eventConfig.containsKey("description")) {
            event.setDescription((String) eventConfig.get("description"));
        }

        if (eventConfig.containsKey("severity")) {
            event.setSeverity(((Number) eventConfig.get("severity")).intValue());
        }
        if (eventConfig.containsKey("reputationEffect")) {
            event.setImmediateReputation(((Number) eventConfig.get("reputationEffect")).intValue());
        }

        // 후속 이벤트 ID 목록 처리
        if (eventConfig.containsKey("subsequentEventIds") && eventConfig.get("subsequentEventIds") != null) {
            @SuppressWarnings("unchecked")
            List<String> subsequentIds = (List<String>) eventConfig.get("subsequentEventIds");
            event.setSubsequentEventIds(subsequentIds);
        }

        event.setStatus(EventStatus.ACTIVE);

        return event;
    }
}
