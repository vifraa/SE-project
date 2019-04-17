package gyarados.splitbackend.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository repository;

    public ChatMessage findById(String id){
        return repository.findById(id).orElseThrow(() -> new ChatMessageNotFoundException(id));
    }

    public List<ChatMessage> findAll(){
        return repository.findAll();
    }

    public ChatMessage add(ChatMessage message){
        return repository.save(message);
    }

    public boolean delete(String id){
        repository.deleteById(id);
        return true;
    }
}
