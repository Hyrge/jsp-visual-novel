package model;

public class EventBus {
    // 싱글톤 패턴 적용
    private static EventBus instance;

    private EventBus() {
    }

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public void emit(String eventName, Object data) {
        // TODO: Implement event emission
    }
}
