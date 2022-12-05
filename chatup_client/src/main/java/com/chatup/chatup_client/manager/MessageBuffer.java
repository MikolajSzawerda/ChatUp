package com.chatup.chatup_client.manager;

import com.chatup.chatup_client.model.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messages.add(msg);
                messages.sort(Comparator.comparing(Message::getTimeCreated));
            }
        });
    }

}
