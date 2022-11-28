package com.chatup.chatup_server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatEndpoint {

    Logger logger = LoggerFactory.getLogger(ChatEndpoint.class);

    @MessageMapping("/channel/{channelID}")
    @SendTo("/topic/channel/{channelID}")
    public String broadcast(@Payload String msg, Principal user, @DestinationVariable String channelID){
        logger.info("User: {} sent: {}", user.getName(), msg);
        return msg;
    }
}
