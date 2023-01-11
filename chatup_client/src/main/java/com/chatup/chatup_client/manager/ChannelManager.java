package com.chatup.chatup_client.manager;

import com.chatup.chatup_client.controller.ChatViewController;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.web.ConnectionHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class ChannelManager {
    private final Logger logger = LoggerFactory.getLogger(ChannelManager.class);
    private ConnectionHandler connectionHandler;
    private ChatViewController chatViewController;
    private final boolean testMode;
    private List<Pair<String, Boolean>> waitingChannels = new ArrayList<>();
    public void addWaitingChannel(String name, boolean isDM) {
        waitingChannels.add(new Pair<>(name, isDM));
    }

    @Autowired
    public ChannelManager(@Value("false") boolean testMode) {
        this.testMode = testMode;
    }

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public void setChatViewController(ChatViewController chatViewController) {
        this.chatViewController = chatViewController;
    }

    private final ObservableList<Channel> standardChannels = FXCollections.observableArrayList();

    public ObservableList<Channel> getStandardChannels() {
        return standardChannels;
    }

    public ObservableList<Channel> getDirectMessages() {
        return directMessages;
    }

    private final ObservableList<Channel> directMessages = FXCollections.observableArrayList();

    public void addChannel(Channel channel) {
        ObservableList<Channel> listToAdd;
        if(channel.getIsDirectMessage()) {
            listToAdd = directMessages;
        }
        else {
            listToAdd = standardChannels;
        }
        if(listToAdd.contains(channel)) return;
        if(testMode) {
            listToAdd.add(channel);
            listToAdd.sort(Comparator.comparing(Channel::getName));
            return;
        }
        listToAdd.add(channel);
        Platform.runLater(() -> {
            listToAdd.sort(Comparator.comparing(Channel::getName));
        });
        connectionHandler.addChannel(channel);
        for(Pair<String, Boolean> waitChannel : waitingChannels) {
            if(waitChannel.getKey().equals(channel.getName()) && waitChannel.getValue().equals(channel.getIsDirectMessage())) {
                chatViewController.changeChannel(channel);
                waitingChannels.remove(waitChannel);
                break;
            }
        }
    }

    public Channel getChannelForMessage(Message message) {
        for(Channel ch : standardChannels) {
            if(ch.getId().equals(message.getChannelID())) {
                return ch;
            }
        }
        for(Channel ch : directMessages) {
            if(ch.getId().equals(message.getChannelID())) {
                return ch;
            }
        }
        return null;
    }
}
