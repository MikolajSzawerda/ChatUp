package com.chatup.chatup_client.web;

import com.chatup.chatup_client.model.TokenRequest;
import com.chatup.chatup_client.model.TokenResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


public class AuthClient {
    private final String baseUrl = "http://localhost:8080";
    private final WebClient webClient;

    public AuthClient() {
        this.webClient = WebClient.create(baseUrl);
    }
    public String authenticate(String username, String password) {
        String token = null;
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
            return null;
        }

        return token;
    }
}
