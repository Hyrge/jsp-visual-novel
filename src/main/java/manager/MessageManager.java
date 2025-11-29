package manager;

import java.util.ArrayList;
import java.util.List;

import model.EventBus;
import model.GameState;
import model.entity.Message;

public class MessageManager {
    private List<Message> messages = new ArrayList<>();
    private GameState gameState;

    public MessageManager(GameState gameState) {
        this.gameState = gameState;
    }

    public void sendMessage(Message message) {
        messages.add(message);
        EventBus.getInstance().emit("MESSAGE_SENT", message);
    }

    public List<Message> getMessages() {
        return messages;
    }
}
