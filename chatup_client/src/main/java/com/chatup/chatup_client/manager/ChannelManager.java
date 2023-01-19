package com.chatup.chatup_client.manager;

import com.chatup.chatup_client.model.channels.Channel;
import com.chatup.chatup_client.model.messaging.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class ChannelManager {
    private final Logger logger = LoggerFactory.getLogger(ChannelManager.class);
    private final boolean testMode;
    private final ObservableList<Channel> standardChannels = FXCollections.observableArrayList();
    private final ObservableList<Channel> directMessages = FXCollections.observableArrayList();

    @Autowired
    public ChannelManager(@Value("false") boolean testMode) {
        this.testMode = testMode;
    }

    public ObservableList<Channel> getStandardChannels() {
        return standardChannels;
    }

    public ObservableList<Channel> getDirectMessages() {
        return directMessages;
    }

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
        Platform.runLater(() -> listToAdd.sort(Comparator.comparing(Channel::getName)));

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

    public void clear() {
        standardChannels.clear();
        directMessages.clear();
    }
}
