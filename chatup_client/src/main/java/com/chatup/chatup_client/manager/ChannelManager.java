package com.chatup.chatup_client.manager;

import com.chatup.chatup_client.model.Channel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class ChannelManager {
    private boolean testMode;
    public ChannelManager(@Value("false") boolean testMode) {
        this.testMode = testMode;
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
        Platform.runLater(() -> {
            listToAdd.add(channel);
            listToAdd.sort(Comparator.comparing(Channel::getName));
        });
        Platform.runLater(() -> {
            checkForDuplicates(channel);
        });
    }
    public void checkForDuplicates(Channel channel) {

    }
}
