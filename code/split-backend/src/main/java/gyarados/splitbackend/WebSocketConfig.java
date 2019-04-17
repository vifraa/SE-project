package gyarados.splitbackend;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocketConfig is responsible for configuring the applications websocket settings.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure the message broker options.
     * @param config The object where we do the configuring.
     */
    @Override
    public void configureMessageBroker (MessageBrokerRegistry config) {
        System.out.println("ConfigureMessageBroker initlialized");

        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/ws");
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Function where we register STOMP over WebSocket endpoints.
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        System.out.println("RegisterStompEndpoints Called");

        registry.addEndpoint("/split").setAllowedOrigins("*").withSockJS();
    }

}
