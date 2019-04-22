package gyarados.splitbackend.group;

/**
 * GroupMessageNotFoundException is an exeption thrown at runtime used when
 * querying databases for Groups where the specified id cannot be found in the database.
 */
public class GroupNotFoundException extends RuntimeException {

    protected GroupNotFoundException(String id){
        super("Could not find group " + id);
    }
}
