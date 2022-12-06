package com.chatup.chatup_server.api;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.JwtRequest;
import com.chatup.chatup_server.domain.JwtResponse;
import com.chatup.chatup_server.domain.UserInfo;
import com.chatup.chatup_server.service.AppUserService;
import com.chatup.chatup_server.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
public class UserEndpoint {
    Logger logger = LoggerFactory.getLogger(UserEndpoint.class);

    private final AuthService authService;
    private final AppUserService appUserService;

    @Autowired
    public UserEndpoint(
            AuthService authService,
            AppUserService appUserService
    ) {
        this.authService = authService;
        this.appUserService = appUserService;
    }

    @PostMapping(path = "/auth")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authRequest) {
        try {
            authService.authenticate(authRequest.username(), authRequest.password());
        } catch(AuthenticationException e) {
            logger.info("Authentication failed, user: " + authRequest.username());
            return ResponseEntity.badRequest().build();
        }

        UserDetails userDetails = appUserService.loadUserByUsername(authRequest.username());
        String token = authService.generateTokenForUser(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping("/me")
    public UserInfo getCurrentUserInfo(Principal user) {
        AppUser appUser = appUserService.loadUserByUsername(user.getName());

        return UserInfo.from(appUser);
    }
}
