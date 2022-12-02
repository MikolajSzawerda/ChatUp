package com.chatup.chatup_server.service.messaging;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.Message;
import com.chatup.chatup_server.repository.AppUserRepository;
import com.chatup.chatup_server.repository.MessageRepository;
import com.chatup.chatup_server.service.AuthService;
import com.chatup.chatup_server.service.JwtTokenService;
import com.chatup.chatup_server.service.utils.InstantService;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final AppUserRepository appUserRepository;
    private final JwtTokenService jwtTokenService;
    private final InstantService instantService;


    public MessageService(MessageRepository messageRepository, AppUserRepository appUserRepository, AuthService authService, JwtTokenService jwtTokenService, InstantService instantService) {
        this.messageRepository = messageRepository;
        this.appUserRepository = appUserRepository;
        this.jwtTokenService = jwtTokenService;
        this.instantService = instantService;
    }

    public Message preserve(String content, Principal user, Long channelID){
        String username = jwtTokenService.getUsernameFromToken(user.getName());
        AppUser appUser = appUserRepository.findAppUserByUsername(username);
        return messageRepository.save(new Message(content, instantService.getNow(), appUser, channelID, false));
    }
}
