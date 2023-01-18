package com.chatup.chatup_server.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.rabbitmq.relay-port}")
    public int relayPort;
    @Value("${spring.rabbitmq.relay-host}")
    public String host;

    private final RabbitInterceptor rabbitInterceptor;
    private final HandshakeHandler handshakeHandler;

    public WebSocketConfig(RabbitInterceptor rabbitInterceptor, HandshakeHandler handshakeHandler) {
        this.rabbitInterceptor = rabbitInterceptor;
        this.handshakeHandler = handshakeHandler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setPathMatcher(new AntPathMatcher((".")));
        config
                .setApplicationDestinationPrefixes("/app")
                .enableStompBrokerRelay("/exchange")
                .setRelayHost(host)
                .setRelayPort(relayPort)
                .setClientLogin("guest")
                .setClientPasscode("guest");
//        config.enableSimpleBroker("/topic", "/queue");
//        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
                .setHandshakeHandler(handshakeHandler)
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/chat")
                .setHandshakeHandler(handshakeHandler)
                .setAllowedOrigins("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(rabbitInterceptor);
    }
}