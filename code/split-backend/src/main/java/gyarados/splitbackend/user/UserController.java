package gyarados.splitbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * The controller for the User endpoints.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * Endpoint to get all users in the database.
     * @return The users.
     */
    @GetMapping
    List<User> all() {
        return userService.all();
    }

    /**
     * Endpoint to get one user based on the id.
     * @param id The id of the user to find.
     * @return The user that was found.
     */
    @GetMapping("/{id}")
    User one(@PathVariable String id) {
        return userService.findById(id);
    }


    /**
     * Endpoint for when we want to check if a user logging in already got an account.
     * @return A map containing the user and also a boolean if the user is in a group.
     */
    @PostMapping("/handle-login")
    Map<String, Object> handleLogin(@RequestBody User user) {
        return userService.handleLogin(user);
    }

}
