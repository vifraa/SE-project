package gyarados.splitbackend.chat;

import gyarados.splitbackend.Group.GroupService;
import gyarados.splitbackend.WebSocketEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

/**
 * ChatController provides the endpoints for the chat parts of the application.
 */
@Controller
public class ChatController {

    // Logger used to log actions in the controller.
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private GroupService groupService;

    @MessageMapping("/find-group")
    @SendToUser("/queue/find-group")
    public String findGroup(ChatMessage message){

        String groupId = groupService.FindMatchingGroup();
        logger.info(message.getSender() + "got matched with group: " + groupId);

        return groupId;
    }


    /**
     * sendMessage sends an ChatMessage to all connections that are subscribed to the endpoint
     * specified in the @SendTo annotation.
     * @param chatMessage The specified chat message to send.
     * @return The ChatMessage that are being sent.
     */
    @MessageMapping("/chat/{groupId}/sendMessage")
    @SendTo("/topic/{groupId}")
    public ChatMessage sendMessage(@DestinationVariable String groupId, @Payload ChatMessage chatMessage){
        chatMessage.setGroupid(groupId);
        chatMessageService.add(chatMessage);
        groupService.addChatMessageToGroup(groupId, chatMessage);
        logger.info("Message sent: " + chatMessage.toString());
        return chatMessage;
    }

    /**
     * addUser sends an ChatMessage to all connections that are subscribed to the endpoint
     * soecified in the @SendTo annotation. The ChatMessage that is sent is to notify that an
     * user has joined the channel.
     * @param chatMessage The message to be sent.
     * @param headerAccessor object to work with message headers.
     * @return The ChatMessage that are being sent.
     */
    @MessageMapping("/chat/{groupId}/addUser")
    @SendTo("/topic/{groupId}")
    public ChatMessage addUser(@DestinationVariable String groupId, @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        groupService.addUserToGroup(groupId, chatMessage.getSender());
        groupService.addChatMessageToGroup(groupId, chatMessage);
        chatMessage.setGroupid(groupId);
        chatMessageService.add(chatMessage);
        logger.info("User added: " + chatMessage.toString());
        headerAccessor.getSessionAttributes().put("sender", chatMessage.getSender());
        return chatMessage;
    }

}
