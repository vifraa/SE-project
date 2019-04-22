package gyarados.splitbackend.group;

import gyarados.splitbackend.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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


    public String FindMatchingGroup(Double destLatitude, Double destLongitude, Double currentLatitude, Double currentLongitude){

        /*

        List<Group> allGroups = repository.findAll();
        List<Group> potentialGroups = new ArrayList<>();


        TODO A group needs a calculated position
        Currently we dont have that, instead you can use the below code to get one users position instead temporarily
        just to get it working.
        group.getUsers().get(0).getCurrentLatitude


        for (Group group: allGroups) {
            // CALCULATE DISTANCE

            // IF GOOD
            potentialGroups.add(group);

            // ELSE CONTINUE
        }


        // Second loop to calculate if destination is close.


        */

        // STEPS

        // CALCULATE BOUNDRY FOR USERS POSITION

        // QUERY THE DATABASE FOR THE GROUPS WITHIN THE RANGE OF ABOVE

        // IF FOUND, JOIN

        // ELSE CREATE NEW


        // Current implementation change to above when it is working.
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


