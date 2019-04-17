package gyarados.splitbackend.chat;

public class ChatMessageNotFoundException extends RuntimeException {

    protected ChatMessageNotFoundException(String id){
        super("Could not find ChatMessage " + id);
    }
}
