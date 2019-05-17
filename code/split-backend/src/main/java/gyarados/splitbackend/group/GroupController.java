package gyarados.splitbackend.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    /**
     * Gets and returns all groups.
     * @return The groups.
     */
    @GetMapping
    public List<Group> all(){
        return groupService.findAll();
    }

    /**
     * Endpoint for getting a group by id.
     * @param id The group to find.
     * @return The found group.
     */
    @GetMapping("/{id}")
    public Group one(@PathVariable String id){
        return groupService.findById(id);
    }

}