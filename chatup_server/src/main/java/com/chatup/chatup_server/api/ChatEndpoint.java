package com.chatup.chatup_server.api;

import com.chatup.chatup_server.domain.Message;
import com.chatup.chatup_server.service.messaging.IncomingMessage;
import com.chatup.chatup_server.service.messaging.MessageService;
import com.chatup.chatup_server.service.messaging.OutgoingEvent;
import com.chatup.chatup_server.service.messaging.OutgoingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatEndpoint {

    Logger logger = LoggerFactory.getLogger(ChatEndpoint.class);
    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final SimpUserRegistry simpUserRegistry;
    private final String MESSAGE_FLAG;

    public ChatEndpoint(MessageService messageService,
                        SimpMessagingTemplate simpMessagingTemplate,
                        RabbitTemplate rabbitTemplate,
                        SimpUserRegistry simpUserRegistry,
                        @Value("${app.events.message}") String message_flag) {
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.simpUserRegistry = simpUserRegistry;
        this.MESSAGE_FLAG = message_flag;
    }

    @MessageMapping("{channelID}")
    @SendTo("/exchange/{channelID}")
    public OutgoingEvent broadcast(@Payload IncomingMessage msg, Principal user, @DestinationVariable Long channelID){
        logger.info("User: {} sent: {}", user.getName(), msg.message());
        Message message = messageService.preserve(msg.message(), user.getName(), channelID);
        return new OutgoingEvent(MESSAGE_FLAG, OutgoingMessage.from(message));
    }
}
