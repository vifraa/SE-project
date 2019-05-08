package gyarados.splitbackend.user;


import gyarados.splitbackend.group.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserRepository repository;

    // This should probably not be here. Handle this dependency better!
    @Autowired
    GroupService groupService;

    public User add(User user){
        return repository.save(user);
    }

    public User findById(String id){
        return repository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Finds and returns all users in the database.
     * @return A list of the users.
     */
    public List<User> all(){
        return repository.findAll();
    }


    /**
     * handleLogin checks if an inputted user is in the database. If it is not it creates it and returns
     * an map with the user and a boolean that say that it is not in any group. If it exists we check if the user
     * is in a group and then returns a map with the user and a boolean with if it has a group or not.
     * @param user The user that we want to work with.
     * @return A map containing the user and a boolean showing if the user has a group or not.
     */
    public Map<String, Object> handleLogin(User user){
        Map<String, Object> returnMap = new HashMap<>();

        User existingUser = repository.findById(user.getUserID()).orElse(null);
        if( existingUser == null){
            // User does not already exist. Create entity.
            User savedUser = repository.save(user);
            returnMap.put("user", savedUser);
            returnMap.put("hasGroup", false);
            returnMap.put("groupID", null);
        }else {
            // User exists. Check if it has a group.
            String groupID = groupService.userIsInGroup(existingUser);
            returnMap.put("user", existingUser);
            if(groupID == null){
                // No group found.s
                returnMap.put("hasGroup", false);
                returnMap.put("groupID", null);
            }else{
                // Group found.
                returnMap.put("hasGroup", true);
                returnMap.put("groupID", groupID);
            }
        }
        return returnMap;
    }
}
