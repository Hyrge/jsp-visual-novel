package service;

import java.util.ArrayList;
import java.util.List;

import model.EventBus;
import model.GameState;
import model.entity.Message;

public class MessageService {
    private List<Message> messages = new ArrayList<>();
    private GameState gameState;

    public void sendMessage(Message message) {
        messages.add(message);
        EventBus.getInstance().emit("MESSAGE_SENT", message);
    }

    public List<Message> getMessages() {
        return messages;
    }
}

