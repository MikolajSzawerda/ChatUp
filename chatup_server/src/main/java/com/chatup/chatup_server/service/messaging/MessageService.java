package com.chatup.chatup_server.service.messaging;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Channel;
import com.chatup.chatup_server.domain.Message;
import com.chatup.chatup_server.repository.AppUserRepository;
import com.chatup.chatup_server.repository.ChannelRepository;
import com.chatup.chatup_server.repository.MessageRepository;
import com.chatup.chatup_server.service.AuthService;
import com.chatup.chatup_server.service.JwtTokenService;
import com.chatup.chatup_server.service.utils.InstantService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final AppUserRepository appUserRepository;
    private final JwtTokenService jwtTokenService;
    private final InstantService instantService;
    private final ChannelRepository channelRepository;
    private final int PAGESIZE;


    public MessageService(MessageRepository messageRepository, AppUserRepository appUserRepository, AuthService authService, JwtTokenService jwtTokenService, InstantService instantService,
                          ChannelRepository channelRepository, @Value("${app.feed.pageSize}") int pageSize) {
        this.messageRepository = messageRepository;
        this.appUserRepository = appUserRepository;
        this.jwtTokenService = jwtTokenService;
        this.instantService = instantService;
        this.channelRepository = channelRepository;
        PAGESIZE = pageSize;
    }

    public Message preserve(String content, String username, Long channelID){
        AppUser appUser = appUserRepository.findAppUserByUsername(username);
        Channel channel = channelRepository.getReferenceById(channelID);
        return messageRepository.save(new Message(content, instantService.getNow(), appUser, channel, false));
    }

    public Page<Message> getLastFeed(Long channelID){
        return messageRepository.getLastFeed(channelID, PageRequest.ofSize(PAGESIZE));
    }

    public Page<Message> getFeedFrom(Long channelID, Long messageID, int page){
        return messageRepository.getFeedFrom(channelID, messageID, PageRequest.of(page, PAGESIZE));
    }

    public List<Message> searchByContent(String content, Set<Long> channels, int page){
        return messageRepository.fuzzySearchByContent(content, channels, PageRequest.of(page, PAGESIZE));
    }
}
