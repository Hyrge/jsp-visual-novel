package factory;

import model.entity.Message;
import model.enums.SenderType;

public class MessageFactory {
    public static Message createMessage(SenderType sender, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        return message;
    }

    // TODO: Add more factory methods as needed
}
