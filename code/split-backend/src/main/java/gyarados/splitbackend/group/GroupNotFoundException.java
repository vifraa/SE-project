package gyarados.splitbackend.group;

/**
 * ChatMessageNotFoundException is an exeption thrown at runtime used when
 * querying databases for ChatMessage where the specified id cannot be found in the database.
 */
public class GroupNotFoundException extends RuntimeException {

    protected GroupNotFoundException(String id){
        super("Could not find group " + id);
    }
}
