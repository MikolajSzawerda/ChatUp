package com.chatup.chatup_server.api;

import com.chatup.chatup_server.service.messaging.MessageService;
import com.chatup.chatup_server.service.messaging.OutgoingMessage;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class SearchEndpoint {
    private final MessageService messageService;

    public SearchEndpoint(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/search")
    List<OutgoingMessage> byContentSearch(@RequestParam("phrase") String phrase,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value="channels") Set<Long> channels){
        return messageService.searchByContent(phrase, channels, page)
                .stream()
                .map(OutgoingMessage::from)
                .collect(Collectors.toList());
    }
}
