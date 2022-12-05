package com.chatup.chatup_client.manager;

import com.chatup.chatup_client.model.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.Objects;

public class MessageBuffer {

    public ObservableList<Message> getMessages() {
        return messages;
    }

    private final ObservableList<Message> messages;

    public MessageBuffer() {
        this.messages = FXCollections.observableArrayList();
    }

    public void addMessage(Message msg){
        for(Message m : messages) {
            if(Objects.equals(m.getMessageID(), msg.getMessageID())) {
                return;
            }
        }
        Platform.runLater(() -> {
            messages.add(msg);
            messages.sort(Comparator.comparing(Message::getTimeCreated));
        });
    }

}
