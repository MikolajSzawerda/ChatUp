package com.chatup.chatup_client.manager;

import com.chatup.chatup_client.model.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;

public class MessageBuffer {
    private final boolean testMode;

    public ObservableList<Message> getMessages() {
        return messages;
    }

    private final ObservableList<Message> messages;

    public MessageBuffer(boolean testMode) {
        this.testMode = testMode;
        this.messages = FXCollections.observableArrayList();
    }

    public void addMessage(Message msg){
        if(messages.contains(msg)) return;
        if(testMode) {
            messages.add(msg);
            messages.sort(Comparator.comparing(Message::getTimeCreated));
            return;
        }
        Platform.runLater(() -> {
            messages.add(msg);
            messages.sort(Comparator.comparing(Message::getTimeCreated));
        });
    }

}
