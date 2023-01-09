package com.chatup.chatup_client.manager;

import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.web.ConnectionHandler;
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
    private final ConnectionHandler connectionHandler;
    private boolean testMode;

    @Autowired
    public ChannelManager(@Value("false") boolean testMode, ConnectionHandler connectionHandler) {
        this.testMode = testMode;
        this.connectionHandler = connectionHandler;
    }
    private ObservableList<Channel> standardChannels = FXCollections.observableArrayList();

    public ObservableList<Channel> getStandardChannels() {
        return standardChannels;
    }

    public ObservableList<Channel> getDirectMessages() {
        return directMessages;
    }

    private ObservableList<Channel> directMessages = FXCollections.observableArrayList();

    public void addChannel(Channel channel) {
        ObservableList<Channel> listToAdd;
        if(channel.getIsDirectMessage()) {
            listToAdd = directMessages;
        }
        else {
            listToAdd = standardChannels;
        }
        if(listToAdd.contains(channel)) {
            return;
        }
        if(testMode) {
            listToAdd.add(channel);
            listToAdd.sort(Comparator.comparing(Channel::getName));
            checkForDuplicates(channel);
            return;
        }
        Platform.runLater(() -> {
            listToAdd.add(channel);
            listToAdd.sort(Comparator.comparing(Channel::getName));
        });
        Platform.runLater(() -> {
            boolean foundDuplicate =  checkForDuplicates(channel);
            if(!foundDuplicate) {
                connectionHandler.addChannel(channel);
            }
        });
    }
    public boolean checkForDuplicates(Channel channel) {
        for(Channel ch : standardChannels) {
            if(ch.getId().equals(channel.getId()) && ch != channel) {
                logger.warn("Duplicate channel found: " + ch + " and " + channel);
                ch.setDuplicateFlag(true);
                channel.setDuplicateFlag(true);
                return true;
            }
        }
        return false;
    }
}
