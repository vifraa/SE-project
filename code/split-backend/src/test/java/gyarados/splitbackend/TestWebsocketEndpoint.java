package gyarados.splitbackend;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gyarados.splitbackend.chat.ChatMessage;
import gyarados.splitbackend.group.Group;
import gyarados.splitbackend.group.GroupService;
import gyarados.splitbackend.user.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestWebsocketEndpoint {
    @Value("${server.port}")
    private int port;
    private String url;


    private static final String ASK_FOR_GROUP_NUMBER = "/ws/find-group";
    private static final String RECIEVE_GROUP_NUMBER = "/user/queue/find-group";

    private static final String SEND_MESSAGE = "/ws/chat/testgroup/sendMessage";
    private static final String ADD_USER_MESSAGE = "/ws/chat/testgroup/addUser";
    private static final String RECIEVE_MESSAGE = "/topic/testgroup";

    private CompletableFuture<Group> groupCompletableFuture;
    private CompletableFuture<ChatMessage> chatMessageCompletableFuture;
    WebSocketStompClient stompClient;

    private Group testGroup;

    @Autowired
    GroupService groupService;


    @Before
    public void setup() {
        groupCompletableFuture = new CompletableFuture<>();
        chatMessageCompletableFuture = new CompletableFuture<>();
        url = "ws://localhost:" + port + "/split";

        stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        //stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.getObjectMapper().registerModule(new JavaTimeModule());
        stompClient.setMessageConverter(converter);

        // We need a group to be able to send messages.
        testGroup = new Group();
        testGroup.setGroupId("testgroup");
        groupService.add(testGroup);
    }

    @After
    public void tearDown() throws Exception {
        // We no longer need it in the database.
        groupService.delete(testGroup);
    }

    @Test
    public void testFindGroupEndpoint() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException {
        //String uuid = UUID.randomUUID().toString();

        StompSession stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);


        stompSession.subscribe(RECIEVE_GROUP_NUMBER, new CreateGroupStompFrameHandler());

        User sendUser = new User();
        sendUser.setName("TEST_NAME");
        sendUser.setUserID("testID");
        sendUser.setCurrentLongitude(57.68);
        sendUser.setCurrentLatitude(11.84);
        sendUser.setDestinationLongitude(57.70);
        sendUser.setDestinationLatitude(11.85);
        stompSession.send(ASK_FOR_GROUP_NUMBER, sendUser);

        // Arguments in get function call determines how long we wait for an answer until throwing an error.
        Group group = groupCompletableFuture.get(10, SECONDS);

        stompSession.disconnect();

        assertNotNull(group);
        //assertTrue(group.getUsers().contains(sendUser));
    }


    @Test
    public void testSendMessage() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException{


        StompSession stompSession = stompClient.connect(url, new StompSessionHandlerAdapter(){
        }).get(1, SECONDS);

        stompSession.subscribe(RECIEVE_MESSAGE, new SendMessageStompFrameHandler());

        ChatMessage message = new ChatMessage();

        User sender = new User();
        sender.setName("testsender");
        sender.setUserID("testID");
        message.setSender(sender);
        message.setContent("Test message entered here.");
        message.setType(ChatMessage.MessageType.CHAT);
        message.setGroupid("testgroup");

        stompSession.send(SEND_MESSAGE, message);

        ChatMessage recievedMessage = chatMessageCompletableFuture.get(5, SECONDS);

        stompSession.disconnect();

        assertNotNull(recievedMessage);

        assertEquals(message.getSender(), recievedMessage.getSender());
        assertEquals(message.getContent(), recievedMessage.getContent());
        assertEquals(message.getGroupid(), recievedMessage.getGroupid());
        assertEquals(message.getContent(), recievedMessage.getContent());
        assertEquals(message.getTimestamp(),recievedMessage.getTimestamp());
    }

    @Test
    public void testMatchingGroup() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException {
        StompSession stompSession = stompClient.connect(url, new StompSessionHandlerAdapter(){
        }).get(1, SECONDS);

        stompSession.subscribe(RECIEVE_GROUP_NUMBER, new SendMessageStompFrameHandler());

        User user = new User();
        user.setName("Central");
        user.setUserID("user1");

        user.setCurrentLongitude(57.68);
        user.setCurrentLatitude(11.84);
        user.setDestinationLongitude(57.70);
        user.setDestinationLatitude(11.85);
        stompSession.send(ASK_FOR_GROUP_NUMBER, user);

        User userTwo = new User();
        userTwo.setName("Molnd");
        userTwo.setUserID("user2");

        userTwo.setCurrentLongitude(57.98);
        userTwo.setCurrentLatitude(11.84);
        userTwo.setDestinationLongitude(57.70);
        userTwo.setDestinationLatitude(11.85);
        stompSession.send(ASK_FOR_GROUP_NUMBER, userTwo);


        User userTwo3 = new User();
        userTwo3.setName("Appp");
        userTwo3.setUserID("user3");

        userTwo3.setCurrentLongitude(55.98);
        userTwo3.setCurrentLatitude(10.84);
        userTwo3.setDestinationLongitude(56.70);
        userTwo3.setDestinationLatitude(13.85);
        stompSession.send(ASK_FOR_GROUP_NUMBER, userTwo3);

        groupService.findMatchingGroup(user);
        groupService.findMatchingGroup(userTwo);
        groupService.findMatchingGroup(userTwo3);

        System.out.println(groupService.findAll().size());

        //Group group1 = groupService.findAll().get(0);
        //Group group2 = groupService.findAll().get(1);


        //assertNotNull(user);
        //System.out.println(group.getUsers().size());
        //System.out.println(groupService.findAll().size());

        //stompSession.disconnect();

    }

    /*@Test
    public void testAddUser() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException {
        StompSession stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        stompSession.subscribe(RECIEVE_MESSAGE, new SendMessageStompFrameHandler());

        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setSender("testsender");
        joinMessage.setGroupid("testgroup");
        joinMessage.setType(ChatMessage.MessageType.JOIN);

        stompSession.send(ADD_USER_MESSAGE, joinMessage);

        ChatMessage recievedMessage = chatMessageCompletableFuture.get(5, SECONDS);

        stompSession.disconnect();

        assertNotNull(recievedMessage);
        assertEquals(joinMessage.getType(), recievedMessage.getType());
        assertEquals(joinMessage.getGroupid(), recievedMessage.getGroupid());
        assertEquals(joinMessage.getSender(), recievedMessage.getSender());
    }*/

    @Test
    public void testRemoveUser() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException {
        StompSession stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);


        stompSession.subscribe(RECIEVE_MESSAGE, new SendMessageStompFrameHandler());
        ChatMessage leaveMessage = new ChatMessage();

        User sender = new User();
        sender.setName("testleaver");
        sender.setUserID("testID");
        leaveMessage.setSender(sender);
        leaveMessage.setGroupid("testgroup");
        leaveMessage.setType(ChatMessage.MessageType.LEAVE);


        stompSession.send(SEND_MESSAGE, leaveMessage);


        ChatMessage recievedMessage = chatMessageCompletableFuture.get(5, SECONDS);

        assertNotNull(recievedMessage);
        assertEquals(leaveMessage.getType(), recievedMessage.getType());
        assertEquals(leaveMessage.getGroupid(), recievedMessage.getGroupid());
        assertEquals(leaveMessage.getSender(), recievedMessage.getSender());

        stompSession.disconnect();
    }



    private class SendMessageStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return ChatMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            chatMessageCompletableFuture.complete((ChatMessage) payload);
        }
    }

    private class CreateGroupStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return Group.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object payload) {

            groupCompletableFuture.complete((Group) payload);
        }
    }


    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }
}

