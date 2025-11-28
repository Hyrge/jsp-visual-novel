package manager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.GameState;
import model.entity.Event;
import model.enums.EventStatus;
import model.enums.EventType;
import model.EventBus;
import util.JsonLoader;
import factory.EventFactory;

public class EventManager {
    private List<Map<String, Object>> eventConfig;
    private GameState gameState;
    private Map<String, Event> activeEvents = new HashMap<>();
    private Map<String, Event> scheduledEvents = new HashMap<>();
    private List<Map<String, Object>> randomEventConfigs = new ArrayList<>();

    public EventManager(GameState gameState) {
        this.gameState = gameState;
    }

    public void loadEventConfig() {
        this.eventConfig = JsonLoader.loadJSON("event_config.json");
        for (Map<String, Object> eventConfig : this.eventConfig) {
            String triggerType = (String) eventConfig.get("triggerType");

            if ("SCHEDULED".equals(triggerType)) {
                Event event = EventFactory.createScheduledEvent(eventConfig);
                event.setStatus(EventStatus.ACTIVE);
                this.scheduledEvents.put(event.getId(), event);

            }
            if ("RANDOM".equals(triggerType)) {
                this.randomEventConfigs.add(eventConfig);
            }

        }

    }

    public void checkEvents(LocalDate today) {

    }

    public List<Event> tryTriggerRandomEvents(LocalDate today) {
        List<Event> triggeredEvents = new ArrayList<>();
        for (Map<String, Object> config : randomEventConfigs) {
            double probability = 0.0;
            if (config.containsKey("probability")) {
                probability = ((Number) config.get("probability")).doubleValue();
            }

            if (Math.random() < probability) {
                Event event = EventFactory.createRandomEvent(config);
                event.setTriggeredAt(today);
                if (event.getTriggeredTime() == null) {
                    event.setTriggeredTime(LocalTime.of(12, 0));
                }

                activeEvents.put(event.getId(), event);
                EventBus.getInstance().emit("EVENT_TRIGGERED", event);
                triggeredEvents.add(event);
            }
        }
        return triggeredEvents;
    }

    public Event triggerEvent(EventType type) {
        Event event = new Event();
        event.setType(type);
        event.setStatus(EventStatus.ACTIVE);
        // ... initialize other fields
        activeEvents.put(event.getId(), event);
        EventBus.getInstance().emit("EVENT_TRIGGERED", event);
        return event;
    }

    public List<Event> getActiveEvents() {
        return activeEvents.values().stream()
                .filter(e -> e.getStatus() == EventStatus.ACTIVE)
                .collect(Collectors.toList());
    }
}
