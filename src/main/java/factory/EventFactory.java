package factory;

import model.entity.Event;
import model.enums.EventType;
import model.enums.EventStatus;
import java.util.Map;

public class EventFactory {

    public Event createFromTemplate(Map<String, Object> eventConfig) {
        Event event = new Event();
        // TODO: Implement event creation logic
        return event;
    }
}
