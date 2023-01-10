package com.chatup.chatup_server.api;

import com.chatup.chatup_server.domain.Channel;
import com.chatup.chatup_server.domain.UserInfo;
import com.chatup.chatup_server.service.AppUserService;
import com.chatup.chatup_server.service.messaging.MessageService;
import com.chatup.chatup_server.service.messaging.OutgoingMessage;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class SearchEndpoint {
    private final MessageService messageService;
    private final AppUserService appUserService;

    public SearchEndpoint(MessageService messageService, AppUserService appUserService) {
        this.messageService = messageService;
        this.appUserService = appUserService;
    }

    @GetMapping("/search/messages")
    List<OutgoingMessage> byContentSearch(@RequestParam("phrase") String phrase,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value="channels") Set<Long> channels,
                                          Principal principal){
        Set<Long> filteredChannels = appUserService.getUserChannelsByUsername(principal.getName()).stream()
                .map(Channel::getId)
                .filter(channels::contains)
                .collect(Collectors.toSet());
        return messageService.searchByContent(phrase, filteredChannels, page)
                .stream()
                .map(OutgoingMessage::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/search/users")
    List<UserInfo> byUsernameSearch(@RequestParam("name") String name,
                                          @RequestParam(value = "page", defaultValue = "0") int page){
        return appUserService.fuzzySearchByUsername(name, page)
                .stream()
                .map(UserInfo::from)
                .collect(Collectors.toList());
    }
}
