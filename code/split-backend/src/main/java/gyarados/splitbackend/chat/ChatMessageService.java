package gyarados.splitbackend.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ChatMessageService is responsible for communicating with the database repository.
 */
@Service
public class ChatMessageService {

    /**
     * The database repository.
     */
    @Autowired
    private ChatMessageRepository repository;

    /**
     * Finds an ChatMessage in the database based on the specified id.
     * @param id The id of the chat message.
     * @return The found ChatMessage.
     * @throws ChatMessageNotFoundException when there is no chatmessage with the specified id.
     */
    public ChatMessage findById(String id){
        return repository.findById(id).orElseThrow(() -> new ChatMessageNotFoundException(id));
    }

    /**
     * Finds all ChatMessages in the database.
     * @return A list containg all the ChatMessages.
     */
    public List<ChatMessage> findAll(){
        return repository.findAll();
    }


    /**
     * Adds an ChatMessage to the database.
     * @param message The message to add.
     * @return Returns the saved ChatMessage.
     */
    public ChatMessage add(ChatMessage message){
        return repository.save(message);
    }

    /**
     * Deletes an ChatMessage based on an inputted id.
     * @param id The id of the ChatMessage.
     */
    public void delete(String id){
        repository.deleteById(id);
    }
}
