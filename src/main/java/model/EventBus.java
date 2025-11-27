package model;

import java.util.Map;

public interface EventBus {
    void emit(String eventName, Map<String, Object> data);
}
