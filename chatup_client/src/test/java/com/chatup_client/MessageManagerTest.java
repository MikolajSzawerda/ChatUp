package com.chatup_client;

import com.chatup.chatup_client.manager.MessageManager;
import com.chatup.chatup_client.model.Channel;
import com.chatup.chatup_client.model.Message;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;


public class MessageManagerTest {

    private MessageManager manager;

    @BeforeAll
    static void setupJavaFX() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setupManager() {
        manager = new MessageManager();
    }

    @Test
    void standardOneMessage() throws InterruptedException {
        Message msg = new Message();
        msg.setMessageID(0L);
        msg.setChannelID(0L);
        Channel channel0 = new Channel(0L, "0", false, false);
        Channel otherChannel = new Channel(1L, "1", false, false);
        manager.addMessage(msg);
        Thread.sleep(100);
        assert manager.getMessageBuffer(otherChannel) != null;
        assert manager.getMessageBuffer(otherChannel).getMessages().size() == 0;
        assert manager.getMessageBuffer(channel0) != null;
        assert manager.getMessageBuffer(channel0).getMessages().size() == 1;
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg);
    }

    @Test
    void duplicateOneMessage() throws InterruptedException {
        Message msg = new Message();
        msg.setMessageID(0L);
        msg.setChannelID(0L);
        Message msg2 = new Message();
        msg2.setMessageID(0L);
        msg2.setMessageID(1L);
        Channel channel0 = new Channel(0L, "0", false, false);
        manager.addMessage(msg);
        Thread.sleep(100);
        assert manager.getMessageBuffer(channel0).getMessages().size() == 1;
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg);
        manager.addMessage(msg2);
        Thread.sleep(100);
        assert manager.getMessageBuffer(channel0).getMessages().size() == 1;
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg);
        assert !manager.getMessageBuffer(channel0).getMessages().contains(msg2);
    }

    @Test
    void sameChannelDiffId() throws InterruptedException {
        // Normal case
        Message msg = new Message();
        msg.setMessageID(0L);
        msg.setChannelID(0L);
        Message msg2 = new Message();
        msg2.setMessageID(1L);
        msg2.setChannelID(0L);
        Channel channel0 = new Channel(0L, "0", false, false);
        manager.addMessage(msg);
        Thread.sleep(100);
        assert manager.getMessageBuffer(channel0).getMessages().size() == 1;
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg);
        manager.addMessage(msg2);
        Thread.sleep(100);
        assert manager.getMessageBuffer(channel0).getMessages().size() == 2;
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg);
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg2);
    }

    @Test
    void diffChannelSameId() throws InterruptedException {
        // Abnormal case - a fake message comes in
        Message msg = new Message();
        msg.setMessageID(0L);
        msg.setChannelID(0L);
        Message msg2 = new Message(); // the fake message
        msg2.setMessageID(0L);
        msg2.setChannelID(1L);
        Channel channel0 = new Channel(0L, "0", false, false);
        Channel channel1 = new Channel(1L, "1", false, false);
        manager.addMessage(msg);
        Thread.sleep(100);
        assert manager.getMessageBuffer(channel0).getMessages().size() == 1;
        assert manager.getMessageBuffer(channel0).getMessages().contains(msg);
        manager.addMessage(msg2);
        Thread.sleep(100);
        assert manager.getMessageBuffer(channel0).getMessages().size() == 0;
        assert manager.getMessageBuffer(channel1).getMessages().size() == 0;

    }
}
