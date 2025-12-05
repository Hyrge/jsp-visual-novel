package service;

import java.util.ArrayList;
import java.util.List;

import model.EventBus;
import model.GameState;
import model.entity.Message;
import model.enums.BusEvent;

public class MessageService {
    private List<Message> messages = new ArrayList<>();
    private EventBus eventBus;

    public MessageService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void sendMessage(Message message) {
        messages.add(message);
        eventBus.emit(BusEvent.MESSAGE_SENT, message);
    }

    public List<Message> getMessages() {
        return messages;
    }
}

