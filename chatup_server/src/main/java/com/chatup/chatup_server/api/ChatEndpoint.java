package com.chatup.chatup_server.api;

import com.chatup.chatup_server.domain.Message;
import com.chatup.chatup_server.service.messaging.IncomingMessage;
import com.chatup.chatup_server.service.messaging.MessageService;
import com.chatup.chatup_server.service.messaging.OutgoingMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatEndpoint {

    Logger logger = LoggerFactory.getLogger(ChatEndpoint.class);
    private final MessageService messageService;

    public ChatEndpoint(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/channel/{channelID}")
    @SendTo("/topic/channel/{channelID}")
    public OutgoingMessage broadcast(@Payload IncomingMessage msg, Principal user, @DestinationVariable Long channelID){
        logger.info("User: {} sent: {}", user.getName(), msg.message());
        Message message = messageService.preserve(msg.message(), user, channelID);
        return OutgoingMessage.from(message);
    }
}
