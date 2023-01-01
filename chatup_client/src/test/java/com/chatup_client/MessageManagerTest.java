package com.chatup_client;

import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;


public class MessageManagerTest {

    private MessageManager manager;

    @BeforeEach
    void setupManager() {
        manager = new MessageManager(true);
    }

    Message createMessage(Long messageID, Long channelID, String content) {
        Message msg = new Message();
        msg.setMessageID(messageID);
        msg.setChannelID(channelID);
        msg.setContent("Test message " + content + " (ID: " + messageID + ")");
        msg.setAuthorID(1L);
        msg.setAuthorUsername("TestUser");
        msg.setAuthorFirstName("Test");
        msg.setAuthorLastName("User");
        msg.setTimeCreated(Instant.now());
        msg.setIsDeleted(false);
        return msg;
    }
    @Test
    void standardOneMessage(){
        Message msg = createMessage(0L, 0L, "standardOneMessage");
        Channel channel0 = new Channel(0L, "0", false, false);
        Channel otherChannel = new Channel(1L, "1", false, false);
        manager.addMessage(msg);
        assert manager.getMessageBuffer(otherChannel) != null;
        assert manager.getMessageBuffer(otherChannel).getMessages().size() == 0;
        assert manager.getMessageBuffer(channel0) != null;
        assert manager.getMessageBuffer(channel0).getMessages().size() == 1;
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg);
    }

    @Test
    void duplicateOneMessage() {
        Message msg = createMessage(0L, 0L, "duplicateOneMessage");
        Message msg2 = new Message(msg);
        Channel channel0 = new Channel(0L, "0", false, false);
        manager.addMessage(msg);
        assert manager.getMessageBuffer(channel0).getMessages().size() == 1;
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg);
        manager.addMessage(msg2);
        assert manager.getMessageBuffer(channel0).getMessages().size() == 1;
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg);
        assert !manager.getMessageBuffer(channel0).getMessages().contains(msg2);
    }

    @Test
    void sameChannelDiffId() {
        // Normal case
        Message msg = createMessage(0L, 0L, "sameChannelDiffId");
        Message msg2 = createMessage(1L, 0L, "sameChannelDiffId");
        Channel channel0 = new Channel(0L, "0", false, false);
        manager.addMessage(msg);
        assert manager.getMessageBuffer(channel0).getMessages().size() == 1;
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg);
        manager.addMessage(msg2);
        assert manager.getMessageBuffer(channel0).getMessages().size() == 2;
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg);
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg2);
    }

    @Test
    void diffChannelSameId(){
        // Abnormal case - a fake message comes in
        Message msg = createMessage(0L, 0L, "diffChannelSameId");
        Message msg2 = createMessage(0L, 1L, "diffChannelSameId"); // the fake message
        Channel channel0 = new Channel(0L, "0", false, false);
        Channel channel1 = new Channel(1L, "1", false, false);
        manager.addMessage(msg);
        assert manager.getMessageBuffer(channel0).getMessages().size() == 1;
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg);
        assert !msg.getDuplicateFlag();
        manager.addMessage(msg2);
        assert manager.getMessageBuffer(channel0).getMessages().size() == 1;
        assert manager.getMessageBuffer(channel1).getMessages().size() == 1;
        assert msg.getDuplicateFlag();
        assert msg2.getDuplicateFlag();

    }
}
