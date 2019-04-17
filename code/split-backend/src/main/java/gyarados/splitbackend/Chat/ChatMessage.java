package gyarados.splitbackend.Chat;

//import javax.persistence.Entity;

// @Entity
public class ChatMessage {

    private MessageType type;
    private String content;
    private String sender;


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


}
