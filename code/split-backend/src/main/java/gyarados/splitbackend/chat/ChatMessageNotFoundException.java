package gyarados.splitbackend.chat;

/**
 * ChatMessageNotFoundException is an exeption thrown at runtime used when
 * querying databases for ChatMessage where the specified id cannot be found in the database.
 */
public class ChatMessageNotFoundException extends RuntimeException {

    protected ChatMessageNotFoundException(String id){
        super("Could not find ChatMessage " + id);
    }
}
