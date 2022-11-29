package com.chatup.chatup_server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, JwtTokenService jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenUtil;
    }

    public void authenticate(String username, String password) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    public String generateTokenForUser(UserDetails user) {
        return jwtTokenService.generateToken(user);
    }

}
