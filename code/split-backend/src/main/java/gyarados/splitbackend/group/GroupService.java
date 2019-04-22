package gyarados.splitbackend.group;

import gyarados.splitbackend.chat.ChatMessage;
import gyarados.splitbackend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * GroupService contains logic to work with Groups and delegate actions to the repository.
 */
@Service
public class GroupService {

    // Repository to work with the Group collection in the database.
    @Autowired
    private GroupRepository repository;


    /**
     * findById finds and returns an Group that matches the id.
     * @param id The id of the group we want to find.
     * @return The group.
     * @throws GroupNotFoundException if there is no group with the given id.
     */
    public Group findById(String id){
        return repository.findById(id).orElseThrow(() -> new GroupNotFoundException(id));
    }

    /**
     * Finds and returns all groups in the collection.
     * @return The groups.
     */
    public List<Group> findAll(){
        return repository.findAll();
    }

    /**
     * Adds a group to the collection.
     * @param group The group we want to save.
     * @return The created group.
     */
    public Group add(Group group){
        return repository.save(group);
    }

    /**
     * addUserToGroup adds an user to the group specified by the groupID parameter.
     * @param groupID The group we want to add the user to.
     * @param user The user we want to add.
     * @return The group after the operation.
     */
    public Group addUserToGroup(String groupID, User user){
        Group group = repository.findById(groupID).orElseThrow(() -> new GroupNotFoundException(groupID));
        group.addUser(user);
        return repository.save(group);
    }

    /**
     * addChatMessageToGroup adds a chatmessage to the group specified by the groupID parameter.
     * @param groupID The group we want to add the chatmessage to.
     * @param message The message we want to add.
     * @return The group after the operation.
     */
    public Group addChatMessageToGroup(String groupID, ChatMessage message){
        Group group = repository.findById(groupID).orElseThrow(() -> new GroupNotFoundException(groupID));
        group.addMessage(message);
        return repository.save(group);
    }


    /**
     * findMatchingGroup is responsible to return a groupID with a group that is a good choice of a group for the user.
     * If no good group exists, a new one is created and the id of that one returned.
     * @param destLatitude
     * @param destLongitude
     * @param currentLatitude
     * @param currentLongitude
     * @return The id of the group.
     */
    public String findMatchingGroup(Double destLatitude, Double destLongitude, Double currentLatitude, Double currentLongitude){

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


