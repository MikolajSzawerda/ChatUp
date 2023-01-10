package com.chatup.chatup_server.config;

import com.chatup.chatup_server.service.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RabbitInterceptor implements ChannelInterceptor {
    Logger logger = LoggerFactory.getLogger(RabbitInterceptor.class);
    private final AmqpAdmin amqpAdmin;
    private final JwtTokenService jwtTokenService;

    public RabbitInterceptor(AmqpAdmin amqpAdmin, JwtTokenService jwtTokenService) {
        this.amqpAdmin = amqpAdmin;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            ConcurrentHashMap<String, String> map = (ConcurrentHashMap<String, String>) accessor.getHeader("simpSessionAttributes");
            String username = map.get("__username__");
            accessor.setDestination("/exchange/"+username);
            logger.info("RECEIVED SUBSCRIPTION");
        }
        return message;
    }
}
