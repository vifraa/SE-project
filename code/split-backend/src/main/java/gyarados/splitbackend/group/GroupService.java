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
     *
     * @param id The id of the group we want to find.
     * @return The group.
     * @throws GroupNotFoundException if there is no group with the given id.
     */
    public Group findById(String id) throws GroupNotFoundException{
        return repository.findById(id).orElseThrow(() -> new GroupNotFoundException(id));
    }

    /**
     * Finds and returns all groups in the collection.
     *
     * @return The groups.
     */
    public List<Group> findAll() {
        return repository.findAll();
    }

    /**
     * Adds a group to the collection.
     *
     * @param group The group we want to save.
     * @return The created group.
     */
    public Group add(Group group) {
        return repository.save(group);
    }

    /**
     * Deletes an group from the collection.
     * @param group The group to delete.
     */
    public void delete(Group group) { repository.delete(group);}
    /**
     * addUserToGroup adds an user to the group specified by the groupID parameter.
     *
     * @param groupID The group we want to add the user to.
     * @param user    The user we want to add.
     * @return The group after the operation.
     */
    public Group addUserToGroup(String groupID, User user) {
        Group group = repository.findById(groupID).orElseThrow(() -> new GroupNotFoundException(groupID));
        group.addUser(user);
        return repository.save(group);
    }

    /**
     * userIsInGroup checks all groups if the inputted user is a member of one of them.
     * @param user The user we want to check.
     * @return True if found in a group. Otherwise false.
     */
    public String userIsInGroup(User user){
        List<Group> groups = repository.findAll();

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
        Group group = repository.findById(groupID).orElseThrow(() -> new GroupNotFoundException(groupID));
        group.addMessage(message);
        return repository.save(group);
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
            Double groupDestLongitude = getGroupDestinationLongitude(group);
            Double groupDestLatitude = getGroupDestinationLatitude(group);
            Double destinationDistance = -1.0;
            if(groupDestLongitude!=null || groupDestLatitude!=null){
                destinationDistance=calcDist(groupDestLatitude, groupDestLongitude, user.getDestinationLatitude(), user.getDestinationLongitude());
            }

                //Add Exception Handling
            if(group.getUsers().size() + user.getNumberOfTravelers() < group.getMAX_GROUP_SIZE()
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
	        matchedGroup  = repository.save(newGroup);

        }
        return matchedGroup;

    }

    public Group removeUserFromGroup(User user, String groupid) {
        Group group = findById(groupid);
        group.removeUser(user);

        //fixme perhaps old groups should be saved somewhere else instead of removed?
        if (group.isEmpty()){
            repository.deleteById(groupid);
            return null;
        }else{
            return repository.save(group);
        }


    }
}





