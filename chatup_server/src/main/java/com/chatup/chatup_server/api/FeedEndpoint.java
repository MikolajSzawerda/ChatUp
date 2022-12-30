package com.chatup.chatup_server.api;

import com.chatup.chatup_server.service.messaging.MessageService;
import com.chatup.chatup_server.service.messaging.OutgoingMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FeedEndpoint {

    private final MessageService messageService;

    public FeedEndpoint(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/last-feed/{channelID}")
    public List<OutgoingMessage> lastFeed(@PathVariable("channelID") Long channelID){
        return messageService.getLastFeed(channelID)
                .stream()
                .map(OutgoingMessage::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/feed/{channelID}")
    public List<OutgoingMessage> feedFrom(@PathVariable("channelID") Long channelID,
                                          @RequestParam("fromMessageID") Long messageID,
                                          @RequestParam(value = "page", defaultValue = "0") int page){
        return messageService.getFeedFrom(channelID, messageID, page)
                .stream()
                .map(OutgoingMessage::from)
                .collect(Collectors.toList());
    }
}
