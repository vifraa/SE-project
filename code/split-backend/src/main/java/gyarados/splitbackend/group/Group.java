package gyarados.splitbackend.group;


import gyarados.splitbackend.chat.ChatMessage;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Group{

    private @Id String groupId;
    private String direction;
    private List<ChatMessage> messages;
    private List<String> users;


    public Group(){
        messages = new ArrayList<>();
        users = new ArrayList<>();
    }




    public void addMessage(ChatMessage message){
        messages.add(message);
    }

    public void addUser(String user){
        users.add(user);
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

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
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
}
