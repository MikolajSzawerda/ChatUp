package com.chatup.chatup_server.config;

import java.io.IOException;

import com.chatup.chatup_server.service.AppUserService;
import com.chatup.chatup_server.service.JwtTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final AppUserService appUserService;
    private final JwtTokenService jwtTokenService;
    private final JwtConfig jwtConfig;

    @Autowired
    JwtRequestFilter(AppUserService appUserService, JwtTokenService jwtTokenService, JwtConfig jwtConfig) {
        this.appUserService = appUserService;
        this.jwtTokenService = jwtTokenService;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(jwtConfig.getAuthorizationHeader());

        if(authHeader == null || !authHeader.startsWith(jwtConfig.getTokenPrefix() + " ")) {
            chain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwtToken = authHeader.substring(jwtConfig.getTokenPrefix().length() + 1);

        try {
            username = jwtTokenService.getUsernameFromToken(jwtToken);
        } catch (ExpiredJwtException e) {
            logger.info("JWT has expired");
        } catch (JwtException e) {
            logger.info("Invalid JWT");
        }

        if (username != null) {
            UserDetails userDetails = null;

            try {
                userDetails = appUserService.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                logger.info("User " + username + " doesn't exist.");
            }

            if(userDetails != null) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }

}
