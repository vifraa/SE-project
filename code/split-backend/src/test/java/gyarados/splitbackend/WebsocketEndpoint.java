package gyarados.splitbackend;

import com.fasterxml.jackson.databind.ObjectWriter;
import gyarados.splitbackend.chat.ChatMessage;
import gyarados.splitbackend.group.Group;
import gyarados.splitbackend.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebsocketEndpoint {
    @Value("${server.port}")
    private int port;
    private String url;


    private static final String ASK_FOR_GROUP_NUMBER = "/ws/find-group";
    private static final String RECIEVE_GROUP_NUMBER = "/user/queue/find-group";

    private CompletableFuture<String> completableFuture;

    @Before
    public void setup() {
        completableFuture = new CompletableFuture<>();
        url = "ws://localhost:" + port + "/split";
    }


    @Test
    public void testFindGroupEndpoint() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException {
        //String uuid = UUID.randomUUID().toString();

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);


        stompSession.subscribe(RECIEVE_GROUP_NUMBER, new CreateGroupStompFrameHandler());
        User sendUser = new User();
        sendUser.setName("TESTNAME");

        stompSession.send(ASK_FOR_GROUP_NUMBER, sendUser);

        String group = completableFuture.get(10, SECONDS);

        assertNotNull(group);
    }


    private class CreateGroupStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture.complete((String) o);
        }
    }


    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }
}

