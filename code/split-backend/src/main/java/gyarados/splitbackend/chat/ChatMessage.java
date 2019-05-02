package gyarados.splitbackend.chat;


import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * ChatMessage is a representation of a message that is sent in chats.
 */
public class ChatMessage {

    private @Id String messageId;
    private String groupid;
    private MessageType type;
    private String content;
    private String sender;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime timestamp = LocalDateTime.now();



    /**
     * MessageType is an enum responsible for declaring the type of the message.
     */
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public ChatMessage(){
    }

    public ChatMessage(MessageType type, String content, String sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public MessageType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }


    @Override
    public String toString() {
        return "Type: " + this.getType() + ", Message: " + this.getContent() + ", Sender: " + this.getSender() + ", Groupid: " + this.getGroupid();
    }
}
