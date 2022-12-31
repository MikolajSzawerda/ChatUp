package com.chatup.chatup_server.web;

import com.chatup.chatup_server.BaseInitializedDbTest;
import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.domain.JwtRequest;
import com.chatup.chatup_server.domain.JwtResponse;
import com.chatup.chatup_server.domain.UserInfo;
import com.chatup.chatup_server.repository.AppUserRepository;
import com.chatup.chatup_server.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserTest  extends BaseInitializedDbTest {
    private final String AUTHENTICATE_ENDPOINT = "/auth";
    private final String GET_CURRENT_LOGGED_IN_USER_ENDPOINT = "/me";

    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    public void testAuthenticate() {
        String token = createUserToken(USER_3);
        JwtRequest jwtRequest = new JwtRequest(USER_3, "test");

        ResponseEntity<JwtResponse> response = getAuthenticateRequest(token, jwtRequest);

        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(jwtTokenService.getUsernameFromToken(response.getBody().token()), USER_3);
    }

    @Test
    public void testUserInfo() {
        AppUser user = appUserRepository.findAppUserByUsername(USER_1);
        ResponseEntity<UserInfo> response = getUserInfoRequest(createUserToken(USER_1));

        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(200));
        assertEquals(response.getBody().id(), user.getId());
        assertEquals(response.getBody().username(), user.getUsername());
        assertEquals(response.getBody().firstName(), user.getFirstName());
        assertEquals(response.getBody().lastName(), user.getLastName());

    }

    protected ResponseEntity<JwtResponse> getAuthenticateRequest(String token, JwtRequest body) {
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .port(PORT)
                .host("localhost")
                .path(AUTHENTICATE_ENDPOINT)
                .build().toUri();

        HttpEntity<JwtRequest> httpBody = new HttpEntity<>(body, createAuthHeaders(token));
        return restTemplate.exchange(uri, HttpMethod.POST, httpBody, JwtResponse.class);
    }

    protected ResponseEntity<UserInfo> getUserInfoRequest(String token){
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .port(PORT)
                .host("localhost")
                .path(GET_CURRENT_LOGGED_IN_USER_ENDPOINT)
                .build().toUri();

        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(createAuthHeaders(token)), UserInfo.class);
    }

}
