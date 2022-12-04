package com.chatup.chatup_client.web;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;

public class AuthClient {
    private static String token = "";
    public static String getToken() { return token; }
    public static boolean login(String username, String password) {
        // TODO: Implement authorization
        // return true if successful, false otherwise
        token = "";
        return true;
    }
}
