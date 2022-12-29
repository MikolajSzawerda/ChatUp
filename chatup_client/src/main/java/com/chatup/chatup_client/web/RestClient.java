package com.chatup.chatup_client.web;

import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.model.UserInfo;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.stream.Collectors;

public class RestClient {
    private final WebClient webClient;
    private final String baseUrl = "http://localhost:8080";
    private final String token;
    public RestClient(String token) {
        this.webClient = WebClient.create(baseUrl);
        this.token = token;
    }

    private <T> T postAsClass(String url, Object body, Class<T> clazz) {
        return webClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(clazz)
                .block();
    }

    private <T> Collection<T> postAsCollection(String url, Object body, Class<T> clazz) {
        return webClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(clazz)
                .collect(Collectors.toList())
                .block();
    }
    private <T> T getAsClass(String uri, Class<T> clazz) {
        return webClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(clazz)
                .block();
    }

    private <T> Collection<T> getAsCollection(String uri, Class<T> clazz) {
        return webClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(clazz)
                .collect(Collectors.toList())
                .block();
    }

    public Collection<Message> getLastFeed(Channel channel) {
        return getAsCollection("/last-feed/" + channel.channelID(), Message.class);
    }

    public Collection<Message> getFeedFrom(Channel channel, Message message, int page) {
        return getAsCollection("/feed/" + channel.channelID() + "?fromMessageID=" + message.getMessageID() + "&page=" + page, Message.class);
    }

    public Collection<Message> getFeedFrom(Channel channel, Message message) {
        return getAsCollection("/feed/" + channel.channelID() + "?fromMessageID=" + message.getMessageID(), Message.class);
    }

    public UserInfo getCurrentUser() {
        return getAsClass("/me", UserInfo.class);
    }
}
