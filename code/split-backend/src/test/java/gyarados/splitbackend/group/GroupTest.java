package gyarados.splitbackend.group;

import gyarados.splitbackend.chat.ChatMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GroupTest {

    private Group testGroup;
    private ChatMessage message;

    @Before
    public void setup() {

        testGroup = new Group();
        testGroup.setGroupId("testgroup");
    }

    @After
    public void tearDown() {

    }

    @Test
    public void addMessage() {
    }

    @Test
    public void addUser() {
    }

    @Test
    public void removeUser() {
    }

    @Test
    public void getGroupId() {
    }

    @Test
    public void getDirection() {
    }

    @Test
    public void getMessages() {
    }

    @Test
    public void getUsers() {
    }

    @Test
    public void getMAX_GROUP_SIZE() {
    }
}