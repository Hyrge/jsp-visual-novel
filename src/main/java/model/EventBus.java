package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import model.enums.BusEvent;

public class EventBus {
    private Map<BusEvent, List<Consumer<Object>>> listeners = new HashMap<>();

    public EventBus() {
    }

    /**
     * 이벤트 구독
     */
    public void subscribe(BusEvent event, Consumer<Object> listener) {
        listeners.computeIfAbsent(event, k -> new ArrayList<>()).add(listener);
    }

    /**
     * 이벤트 발행
     */
    public void emit(BusEvent event, Object data) {
        if (listeners.containsKey(event)) {
            for (Consumer<Object> listener : listeners.get(event)) {
                listener.accept(data);
            }
        }
    }
}
