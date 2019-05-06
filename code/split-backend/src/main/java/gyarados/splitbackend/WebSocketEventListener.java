package gyarados.splitbackend;

import gyarados.splitbackend.chat.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * WebSocketEventListener is an event listener for listening on websocket connections.
 */
@Component
public class WebSocketEventListener {

    // Logger to log information to the console.
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    // Contains methods for use for STOMP
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    /**
     * Runs when an connection to the websocket is made.
     * @param event The connection event.
     */
    @EventListener
    public void handleWebSocketConnectionListener(SessionConnectedEvent event){
        logger.info("Recieved a new web socket connection");
    }


    /**
     * Runs when someone disconnects from the websocket. Creates an LEAVE message and sends it
     * to the chat.
     * @param event The disconnect event.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("sender");
        if(username != null){
            logger.info("User disconnected: " + username);

        }else{
            logger.info("Unknown user disconnected");
        }
    }



}
