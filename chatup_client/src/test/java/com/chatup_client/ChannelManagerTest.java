package com.chatup_client;

import com.chatup.chatup_client.manager.ChannelManager;
import com.chatup.chatup_client.model.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChannelManagerTest {
    private ChannelManager channelManager;

    @BeforeEach
    void setupChannelManager() {
        channelManager = new ChannelManager(true);
    }

    private Channel createChannel(Long id, String name, boolean isDM, boolean isPrivate) {
        Channel ch = new Channel();
        ch.setId(id);
        ch.setName(name);
        ch.setIsDirectMessage(isDM);
        ch.setIsPrivate(isPrivate);
        return ch;
    }

    @Test
    void standardOneChannel() {
        Channel channel = createChannel(0L, "Standard", false, false);
        channelManager.addChannel(channel);
        assertEquals(1, channelManager.getStandardChannels().size());
        assertTrue(channelManager.getStandardChannels().contains(channel));
    }

    @Test
    void sortingTest() {
        Channel channelStandard = createChannel(0L, "Standard", false, false);
        Channel channelDM = createChannel(1L, "DM", true, false);
        channelManager.addChannel(channelStandard);
        channelManager.addChannel(channelDM);
        assertEquals(1, channelManager.getStandardChannels().size());
        assertTrue(channelManager.getStandardChannels().contains(channelStandard));
        assertEquals(1, channelManager.getDirectMessages().size());
        assertTrue(channelManager.getDirectMessages().contains(channelDM));
    }

    @Test
    void sameChannelTwice() {
        Channel channel = createChannel(0L, "Standard", false, false);
        Channel channelCopy = new Channel(channel);
        channelManager.addChannel(channel);
        channelManager.addChannel(channelCopy);
        assertEquals(1, channelManager.getStandardChannels().size());
        assertTrue(channelManager.getStandardChannels().contains(channel));
        for(Channel ch : channelManager.getStandardChannels()) {
            assert ch != channelCopy;
        }
    }
}
