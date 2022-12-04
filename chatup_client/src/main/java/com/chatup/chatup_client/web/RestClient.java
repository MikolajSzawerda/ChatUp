package com.chatup.chatup_client.web;

import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.stream.Collectors;

public class RestClient {
    private final WebClient webClient;
    private final String baseUrl = "http://localhost:8080";
    private String token;
    public RestClient(String token) {
        this.webClient = WebClient.create(baseUrl);
        this.token = token;
    }

    public Collection<Message> getLastFeed(Channel channel) {
        return webClient.get()
                .uri("/last-feed/" + channel.channelID())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Message.class)
                .collect(Collectors.toList())
                .block();
    }

    public Collection<Message> getFeedFrom(Channel channel, Long messageID, int page) {
        return webClient.get()
                .uri("/feed/" + channel.channelID() + "?fromMessageID=" + messageID + "&page=" + page)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Message.class)
                .collect(Collectors.toList())
                .block();
    }

    public Collection<Message> getFeedFrom(Channel channel, Long messageID) {
        return webClient.get()
                .uri("/feed/" + channel.channelID() + "?fromMessageID=" + messageID)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Message.class)
                .collect(Collectors.toList())
                .block();
    }
}
