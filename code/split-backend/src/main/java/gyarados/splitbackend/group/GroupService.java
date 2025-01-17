package gyarados.splitbackend.group;

import com.mongodb.WriteResult;
import com.mongodb.client.result.UpdateResult;
import gyarados.splitbackend.chat.ChatMessage;
import gyarados.splitbackend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * GroupService contains logic to work with Groups and delegate actions to the repository.
 */
@Service
public class GroupService {

    // Repository to work with the Group collection in the database.
    @Autowired
    private GroupRepository groupRepository;


    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * findById finds and returns an Group that matches the id.
     *
     * @param id The id of the group we want to find.
     * @return The group.
     * @throws GroupNotFoundException if there is no group with the given id.
     */
    public Group findById(String id) throws GroupNotFoundException{
        return groupRepository.findById(id).orElseThrow(() -> new GroupNotFoundException(id));
    }

    /**
     * Finds and returns all groups in the collection.
     *
     * @return The groups.
     */
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    /**
     * Adds a group to the collection.
     *
     * @param group The group we want to save.
     * @return The created group.
     */
    public Group add(Group group) {
        return groupRepository.save(group);
    }

    /**
     * Deletes an group from the collection.
     * @param group The group to delete.
     */
    public void delete(Group group) { groupRepository.delete(group);}
    /**
     * addUserToGroup adds an user to the group specified by the groupID parameter.
     *
     * @param groupID The group we want to add the user to.
     * @param user    The user we want to add.
     * @return The group after the operation.
     */
    public Group addUserToGroup(String groupID, User user) {
        Group group = groupRepository.findById(groupID).orElseThrow(() -> new GroupNotFoundException(groupID));
        group.addUser(user);
        return groupRepository.save(group);
        
    }

    /**
     * userIsInGroup checks all groups if the inputted user is a member of one of them.
     * @param user The user we want to check.
     * @return True if found in a group. Otherwise false.
     */
    public String userIsInGroup(User user){
        List<Group> groups = groupRepository.findAll();

        for (Group group: groups){
            if(group.getUsers().contains(user)){
                return group.getGroupId();
            }
        }
        return null;
    }

    /**
     * addChatMessageToGroup adds a chatmessage to the group specified by the groupID parameter.
     *
     * @param groupID The group we want to add the chatmessage to.
     * @param message The message we want to add.
     * @return The group after the operation.
     */
    public Group addChatMessageToGroup(String groupID, ChatMessage message) {
        Group group = groupRepository.findById(groupID).orElseThrow(() -> new GroupNotFoundException(groupID));
        group.addMessage(message);
        return groupRepository.save(group);
    }

    /**
     * Calculates the distance between two points (x1,y1) and (x2,y2)
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return distance between points
     */
    public Double calcDist(Double x1, Double y1, Double x2, Double y2) {
        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        return distance;
    }

    /**
     * Retrieves the current longitude of a Group
     */
    public Double getGroupCurrentLongitude(Group group) {
        Double groupLongitude = group.getUsers().get(0).getCurrentLongitude();
        return groupLongitude;
    }

    /**
     * Retrieves the current latitude of a Group
     */
    public Double getGroupCurrentLatitude(Group group) {
        Double groupLatitude = group.getUsers().get(0).getCurrentLatitude();
        return groupLatitude;
    }

    /**
     * Retrieves the destination latitude of a Group
     */
    public Double getGroupDestinationLatitude(Group group) {
        List<User> users = group.getUsers();
        if(users.size()>0){
            User firstUser = group.getUsers().get(0);
            if(firstUser!=null){
                Double groupLatitude = firstUser.getDestinationLatitude();
                return groupLatitude;
            }
        }

        return null;
    }

    /**
     * Retrieves the destination longitude of a Group
     */
    public Double getGroupDestinationLongitude(Group group) {
        List<User> users = group.getUsers();
        if(users.size()>0){
            User firstUser = group.getUsers().get(0);
            if(firstUser!=null){
                Double groupLongitude = firstUser.getDestinationLongitude();
                return groupLongitude;
            }
        }

        return null;

    }

    /**
     * findMatchingGroup is responsible to return a groupID with a group that is a good choice of a group for the user.
     * If no good group exists, a new one is created and the id of that one returned.
     * @param user
     * @return The id of the group.
     */
    public Group findMatchingGroup(User user) {

        List<Group> allGroups = findAll();
        List<Group> potentialGroups = new ArrayList<Group>();
        Group matchedGroup = null;
        /*TODO A group needs a calculated position
        Currently we dont have that, instead you can use the below code to get one users position instead temporarily
        just to get it working.
        this.currentLongitude = group.getUsers().get(0).getCurrentLongitude
		*/

        // 1) List all possible groups within a maximum destination distance from users preferred distance
        for (Group group: allGroups) {

            // Dont join a group where the user previously has been in.
            if(group.getPreviousUsers().contains(user)){
                continue;
            }

            Double groupDestLongitude = getGroupDestinationLongitude(group);
            Double groupDestLatitude = getGroupDestinationLatitude(group);
            Double destinationDistance = -1.0;
            if(groupDestLongitude!=null || groupDestLatitude!=null){
                destinationDistance=calcDist(groupDestLatitude, groupDestLongitude, user.getDestinationLatitude(), user.getDestinationLongitude());
            }
            int totalGroupMembers = 0;
            for (User u: group.getUsers()) {
            	totalGroupMembers += u.getNumberOfTravelers();
            }
            	
            
            
                //Add Exception Handling
            if(totalGroupMembers + user.getNumberOfTravelers() <= group.getMAX_GROUP_SIZE()
                    && group.getUsers().size() > 0
                    && destinationDistance <= 0.05 && destinationDistance >= 0) {
                potentialGroups.add(group);
            }

        }

        Double matchedDistance = 0.0;
        //2) Loop and choose the group with the minimum current distance from users current distance
        for (Group group: potentialGroups) {
        		Double groupCurrentLongitude = getGroupCurrentLongitude(group);
        		Double groupCurrentLatitude = getGroupCurrentLatitude(group);
        		Double currentDistance = calcDist(groupCurrentLatitude, groupCurrentLongitude, user.getCurrentLatitude(), user.getCurrentLongitude());

        		if(currentDistance <= 0.05 && currentDistance >=0 && (currentDistance < matchedDistance || matchedGroup == null)) {

        			matchedDistance = currentDistance;
        			matchedGroup = group;	
        		}
        		
        		
         }
	    if(matchedGroup == null){
	        Group newGroup = new Group();
	        matchedGroup  = groupRepository.save(newGroup);

        }
        return matchedGroup;

    }

    public Group removeUserFromGroup(User user, String groupid) {
        Group group = findById(groupid);
        group.removeUser(user);

        // When a group contains 0 members it should probably be moved to another collection.
        // Otherwise we loop through empty lists when trying to find a group which makes n of O(n) greater.
        return groupRepository.save(group);

    }


    public void updateUserInGroup(User user, String groupId){
        Group group = findById(groupId);
        group.removeUser(user);
        group.addUser(user);

        Query query = new Query(new Criteria().andOperator(
                Criteria.where("_id").is(groupId),
                Criteria.where("users").elemMatch(Criteria.where("_id").is(user.getUserID()))
        ));

        Update update = new Update().set("users.$.photoUrl", user.getPhotoUrl());

        UpdateResult ur = mongoTemplate.updateFirst(query,update, "group");
    }


    /**
     * getGroupChatMessages returns an list of a groups previous chat messages.
     * @param id The id of the group.
     * @return The list of messages.
     */
    public List<ChatMessage> getGroupChatMessages(String id){
        return findById(id).getMessages();
    }

    /**
     * findAllPrevious returns a groups all previous users.
     * @param id The id of the group.
     * @return The list of previous users.
     */
    public List<User> findAllPrevious(String id){
        Group group = findById(id);
        return group.getPreviousUsers();
    }
}





