package com.chatup_client;

import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.channels.Channel;
import com.chatup.chatup_client.model.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;


public class MessageManagerTest {

    private MessageManager manager;

    @BeforeEach
    void setupManager() {
        manager = new MessageManager(true, null);
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
    Channel createChannel(Long channelID) {
        Channel ch = new Channel();
        ch.setId(channelID);
        ch.setName(channelID.toString());
        ch.setIsPrivate(false);
        ch.setIsDirectMessage(false);
        return ch;
    }
    @Test
    void standardOneMessage(){
        Message msg = createMessage(0L, 0L, "standardOneMessage");
        Channel channel0 = createChannel(0L);
        Channel otherChannel = createChannel(1L);
        manager.addMessage(msg);
        assertNotEquals(null, manager.getMessageBuffer(otherChannel.getId()));
        assertEquals(0, manager.getMessageBuffer(otherChannel.getId()).getMessages().size());
        assertNotEquals(null, manager.getMessageBuffer(channel0.getId()));
        assertEquals(1, manager.getMessageBuffer(channel0.getId()).getMessages().size());
        assertTrue(manager.getMessageBuffer(channel0.getId()).getMessages().contains(msg));
    }

    @Test
    void duplicateOneMessage() {
        Message msg = createMessage(0L, 0L, "duplicateOneMessage");
        Message msg2 = new Message(msg);
        Channel channel0 = createChannel(0L);
        manager.addMessage(msg);
        assertEquals(1, manager.getMessageBuffer(channel0.getId()).getMessages().size());
        assertTrue(manager.getMessageBuffer(channel0.getId()).getMessages().contains(msg));
        manager.addMessage(msg2);
        assertEquals(1, manager.getMessageBuffer(channel0.getId()).getMessages().size());
        assertTrue(manager.getMessageBuffer(channel0.getId()).getMessages().contains(msg));
        for(Message m : manager.getMessageBuffer(channel0.getId()).getMessages()) {
            assert m != msg2;
        }
    }

    @Test
    void sameChannelDiffId() {
        // Normal case
        Message msg = createMessage(0L, 0L, "sameChannelDiffId");
        Message msg2 = createMessage(1L, 0L, "sameChannelDiffId");
        Channel channel0 = createChannel(0L);
        manager.addMessage(msg);
        assertEquals(1, manager.getMessageBuffer(channel0.getId()).getMessages().size());
        assertTrue(manager.getMessageBuffer(channel0.getId()).getMessages().contains(msg));
        manager.addMessage(msg2);
        assertEquals(2, manager.getMessageBuffer(channel0.getId()).getMessages().size());
        assertTrue(manager.getMessageBuffer(channel0.getId()).getMessages().contains(msg));
        assertTrue(manager.getMessageBuffer(channel0.getId()).getMessages().contains(msg2));
    }
}
