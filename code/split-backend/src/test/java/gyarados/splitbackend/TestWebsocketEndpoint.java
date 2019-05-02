package gyarados.splitbackend;

import gyarados.splitbackend.chat.ChatMessage;
import gyarados.splitbackend.group.Group;
import gyarados.splitbackend.group.GroupService;
import gyarados.splitbackend.user.User;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
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
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

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
        message.setSender("TestSender");
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

