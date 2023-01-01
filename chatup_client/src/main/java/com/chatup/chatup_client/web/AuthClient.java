package com.chatup.chatup_client.web;

import com.chatup.chatup_client.config.AppConfig;
import com.chatup.chatup_client.model.TokenRequest;
import com.chatup.chatup_client.model.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class AuthClient {
    private final Logger logger = LoggerFactory.getLogger(AuthClient.class);
    private final AppConfig appConfig;
    private final WebClient webClient;

    public String getToken() {
        return token;
    }

    private String token;

    @Autowired
    public AuthClient(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.webClient = WebClient.create(appConfig.getRestURL());
        logger.info("AuthClient created");
    }
    public boolean authenticate(String username, String password) {
        try {
            TokenResponse tokenResponse = webClient.post()
                .uri("/auth")
                .body(Mono.just(new TokenRequest(username, password)), TokenRequest.class)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

            if(tokenResponse != null)
                token = tokenResponse.getToken();

        } catch(WebClientResponseException e) {
            return false;
        }
        return true;
    }
}
