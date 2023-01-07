package com.chatup.chatup_client.web;

import com.chatup.chatup_client.config.AppConfig;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.model.UserInfo;
import com.chatup.chatup_client.model.messaging.ChannelCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RestClient {
    private final Logger logger = LoggerFactory.getLogger(RestClient.class);
    private final WebClient webClient;

    private final AppConfig appConfig;
    private final AuthClient authClient;
    public RestClient(AuthClient authClient, AppConfig appConfig) {
        this.authClient = authClient;
        this.appConfig = appConfig;
        this.webClient = WebClient.create(appConfig.getRestURL());
        logger.info("RestClient created");
    }

    private <T> T postAsClass(String url, Object body, Class<T> clazz) {
        return webClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + authClient.getToken())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(clazz)
                .block();
    }

    private <T> Collection<T> postAsCollection(String url, Object body, Class<T> clazz) {
        return webClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + authClient.getToken())
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(clazz)
                .collect(Collectors.toList())
                .block();
    }
    private <T> T getAsClass(String uri, Class<T> clazz) {
        return webClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + authClient.getToken())
                .retrieve()
                .bodyToMono(clazz)
                .block();
    }

    private <T> Collection<T> getAsCollection(String uri, Class<T> clazz) {
        return webClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + authClient.getToken())
                .retrieve()
                .bodyToFlux(clazz)
                .collect(Collectors.toList())
                .block();
    }

    public Collection<Message> getLastFeed(Channel channel) {
        return getAsCollection("/last-feed/" + channel.id(), Message.class);
    }

    public Collection<Message> getFeedFrom(Channel channel, Message message, int page) {
        return getAsCollection("/feed/" + channel.id() + "?fromMessageID=" + message.getMessageID() + "&page=" + page, Message.class);
    }

    public Collection<Message> getFeedFrom(Channel channel, Message message) {
        return getAsCollection("/feed/" + channel.id() + "?fromMessageID=" + message.getMessageID(), Message.class);
    }

    public UserInfo getCurrentUser() {
        return getAsClass("/me", UserInfo.class);
    }
    public Channel createChannel(String name, boolean isPrivate, boolean isDirectMessage, Set<Long> userIDs) {
        return postAsClass("/channel/create", new ChannelCreateRequest(name, isPrivate, isDirectMessage, userIDs), Channel.class);
    }
    public Collection<Channel> listChannels() {
        return getAsCollection("/channel/list", Channel.class);
    }

}
