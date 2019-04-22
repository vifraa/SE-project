package gyarados.splitbackend.group;

import gyarados.splitbackend.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository repository;

    public Group findById(String id){
        return repository.findById(id).orElseThrow(() -> new GroupNotFoundException(id));
    }

    public List<Group> findAll(){
        return repository.findAll();
    }

    public Group add(Group group){
        return repository.save(group);
    }

    public Group addUserToGroup(String groupID, String username){
        Group group = repository.findById(groupID).orElseThrow(() -> new GroupNotFoundException(groupID));
        group.addUser(username);
        return repository.save(group);
    }

    public Group addChatMessageToGroup(String groupID, ChatMessage message){
        Group group = repository.findById(groupID).orElseThrow(() -> new GroupNotFoundException(groupID));
        group.addMessage(message);
        return repository.save(group);
    }


    public String FindMatchingGroup(){
        List<Group> groups = findAll();

        if(groups.size() > 0){
            return groups.get(0).getGroupId();
        }else{
            Group newGroup = new Group();
            Group createdGroup = repository.save(newGroup);
            return createdGroup.getGroupId();
        }

    }

}


