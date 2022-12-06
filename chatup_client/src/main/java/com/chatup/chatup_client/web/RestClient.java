package com.chatup.chatup_client.web;

import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.MeObject;
import com.chatup.chatup_client.model.Message;
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

    public Collection<Message> getLastFeed(Channel channel) {
        return webClient.get()
                .uri("/last-feed/" + channel.channelID())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Message.class)
                .collect(Collectors.toList())
                .block();
    }

    public Collection<Message> getFeedFrom(Channel channel, Message message, int page) {
        return webClient.get()
                .uri("/feed/" + channel.channelID() + "?fromMessageID=" + message.getMessageID() + "&page=" + page)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Message.class)
                .collect(Collectors.toList())
                .block();
    }

    public Collection<Message> getFeedFrom(Channel channel, Message message) {
        return webClient.get()
                .uri("/feed/" + channel.channelID() + "?fromMessageID=" + message.getMessageID())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(Message.class)
                .collect(Collectors.toList())
                .block();
    }

    public MeObject getMe() {
        return webClient.get()
                .uri("/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(MeObject.class)
                .block();
    }
}
