package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
    private Map<String, List<Consumer<Object>>> listeners = new HashMap<>();

    private EventBus() {
    }

    public void subscribe(String eventName, Consumer<Object> listener) {
        listeners.computeIfAbsent(eventName, k -> new ArrayList<>()).add(listener);
    }

    public void emit(String eventName, Object data) {
        if (listeners.containsKey(eventName)) {
            for (Consumer<Object> listener : listeners.get(eventName)) {
                listener.accept(data);
            }
        }
    }
}
