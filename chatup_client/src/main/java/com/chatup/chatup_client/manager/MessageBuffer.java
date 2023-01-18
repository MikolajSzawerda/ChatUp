package com.chatup.chatup_client.manager;

import com.chatup.chatup_client.manager.exception.OutOfMessagesException;
import com.chatup.chatup_client.model.Message;
import com.chatup.chatup_client.web.RestClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;

import javax.management.ObjectInstance;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class MessageBuffer {
    private final boolean testMode;
    private final RestClient restClient;
    private final Long channelID;
    private final Logger logger;


    public Long getMessageLoaded() {
        return messageLoaded;
    }

    private Long messageLoaded;
    private boolean initialized;
    private List<Object> registeredGUIObjects;
    public void registerGUIObject(Object obj) {
        registeredGUIObjects.add(obj);
    }


    public ObservableList<Message> getMessages() {
        return messages;
    }

    private final ObservableList<Message> messages;

    public MessageBuffer(boolean testMode, RestClient restClient, Long channelID, Logger logger) {
        this.testMode = testMode;
        this.restClient = restClient;
        this.channelID = channelID;
        this.logger = logger;
        this.messages = FXCollections.observableArrayList();
    }

    public void addMessage(Message msg){
        if(messages.contains(msg)) return;
        if(testMode) {
            messages.add(msg);
            messages.sort(Comparator.comparing(Message::getTimeCreated));
            return;
        }
        messages.add(msg);
        Platform.runLater(() -> {
            messages.sort(Comparator.comparing(Message::getTimeCreated));
        });
    }

    public void loadNextMessagesGUI(Object guiObject) throws OutOfMessagesException {
        if(registeredGUIObjects.contains(guiObject)) {
            registeredGUIObjects.remove(guiObject);
            loadNextMessages();
        }
    }
    public void loadNextMessages() throws OutOfMessagesException {
        synchronized (this) {
            Collection<Message> messages;
            if(initialized) {
                logger.info("Requesting messages from messageID " + (messageLoaded) + " for channelID: " + channelID);
                messages = restClient.getFeedFrom(channelID, messageLoaded);
            }
            else {
                logger.info("Requesting initial messages for channelID " + channelID);
                messages = restClient.getLastFeed(channelID);
            }
            if(messages.size() == 0) {
                throw new OutOfMessagesException();
            }
            if(!initialized) {
                initialized = true;
                logger.info("Initializing message buffer for channelID " + channelID);
            }
            logger.info("Loading messages for channelID " + channelID);
            messages.forEach(this::addMessage);
            Long minID = -1L;
            for(Message m : messages) {
                if(minID == -1L) {
                    minID = m.getMessageID();
                }
                if(m.getMessageID() < minID) {
                    minID = m.getMessageID();
                }
            }
            messageLoaded = minID;
        }
    }
}
