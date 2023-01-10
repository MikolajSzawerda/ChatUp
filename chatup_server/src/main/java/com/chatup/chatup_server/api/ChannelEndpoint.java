package com.chatup.chatup_server.api;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Channel;
import com.chatup.chatup_server.service.AppUserService;
import com.chatup.chatup_server.service.channels.ChannelCreateRequest;
import com.chatup.chatup_server.service.channels.ChannelInfo;
import com.chatup.chatup_server.service.channels.ChannelService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/channel")
public class ChannelEndpoint {
    Logger logger = LoggerFactory.getLogger(ChannelEndpoint.class);

    private final ChannelService channelService;
    private final AppUserService appUserService;

    public ChannelEndpoint(ChannelService channelService, AppUserService appUserService) {
        this.channelService = channelService;
        this.appUserService = appUserService;
    }

    @PostMapping("/create")
    public ChannelInfo createChannel(Principal user, @Valid @RequestBody ChannelCreateRequest channelCreateRequest){
        AppUser currentUser = appUserService.loadUserByUsername(user.getName());

        if (!channelCreateRequest.user_ids().contains(currentUser.getId()))
            throw new IllegalArgumentException("Current user must be contained in the new channel.");

        Channel newChannel;
        newChannel = channelService.createChannel(channelCreateRequest);

        logger.info("New channel created, id: " + newChannel.getId());

        return ChannelInfo.from(currentUser.getId(), newChannel);
    }

    @GetMapping("/list")
    public List<ChannelInfo> listChannelsInfo(Principal user) {
        AppUser currentUser = appUserService.loadUserByUsername(user.getName());
        return channelService.listChannelsInfo(currentUser);
    }
}
