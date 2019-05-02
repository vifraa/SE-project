package gyarados.splitbackend.group;


import gyarados.splitbackend.chat.ChatMessage;
import gyarados.splitbackend.user.User;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Group contains logic related to a group.
 */
public class Group{

    private @Id String groupId;
    private String direction;
    private List<ChatMessage> messages;
    private List<User> users;
    private final int MAX_GROUP_SIZE = 4;



    public Group(){
        messages = new ArrayList<>();
        users = new ArrayList<>();
    }




    public void addMessage(ChatMessage message){
        messages.add(message);
    }

    public void addUser(User user){
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "group{" +
                "groupId='" + groupId + '\'' +
                ", direction='" + direction + '\'' +
                ", messages=" + messages +
                '}';
    }

    public int getMAX_GROUP_SIZE() {
        return MAX_GROUP_SIZE;
    }
}
