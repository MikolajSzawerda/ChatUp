package com.chatup.chatup_server.api;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Channel;
import com.chatup.chatup_server.domain.exceptions.InvalidRequestException;
import com.chatup.chatup_server.service.AppUserService;
import com.chatup.chatup_server.service.channels.ChannelCreateRequest;
import com.chatup.chatup_server.service.channels.ChannelInfo;
import com.chatup.chatup_server.service.channels.ChannelService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<?> createChannel(Principal user, @Valid @RequestBody ChannelCreateRequest channelCreateRequest) {
        AppUser currentUser = appUserService.loadUserByUsername(user.getName());

        if (!channelCreateRequest.user_ids().contains(currentUser.getId()))
            return ResponseEntity.badRequest().body("Current user must be contained in the new channel.");

        Channel newChannel;
        try {
            newChannel = channelService.createChannel(channelCreateRequest);
        } catch (InvalidRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        logger.info("New channel created, id: " + newChannel.getId());

        return ResponseEntity.ok(ChannelInfo.from(currentUser.getId(), newChannel));
    }

    @GetMapping("/list")
    public ResponseEntity<?> listChannelsInfo(Principal user) {
        AppUser currentUser = appUserService.loadUserByUsername(user.getName());
        List<ChannelInfo> channelsInfo = channelService.listChannelsInfo(currentUser);

        return ResponseEntity.ok(channelsInfo);
    }
}
