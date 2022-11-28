package com.chatup.chatup_server.web;

import com.chatup.chatup_server.BaseIntegrationTest;
import com.chatup.chatup_server.client.SocketClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatTest extends BaseIntegrationTest {

    @Autowired
    private SimpUserRegistry simpUserRegistry;
    SocketClient client1;
    SocketClient client2;
    private static final String channel="abc";
    private static final LinkedList<String> topics = new LinkedList<>(){{
        add("/topic/channel/"+channel);
    }};

    private static final String BROADCAST_ENDPOINT = "/app/channel/"+channel;

    @BeforeEach
    void inintClient(){
        client1 = socketClientFactory.getClient(topics);
        client2 = socketClientFactory.getClient(topics);
    }

    @AfterEach
    void closeConnections(){
        client1.close();
        client2.close();
    }

    @Test
    void shouldCreateConnections(){
        timedAssertEquals(2, simpUserRegistry::getUserCount);
        SocketClient client1 = socketClientFactory.getClient(topics);
        SocketClient client2 = socketClientFactory.getClient(topics);
        timedAssertEquals(4, simpUserRegistry::getUserCount);
        client1.close();
        client2.close();
        timedAssertEquals(2, simpUserRegistry::getUserCount);
    }

    @Test
    void shouldBroadcastMessages(){
        String msg = "Test";
        client1.sendMessage(BROADCAST_ENDPOINT, msg);
        timedAssertEquals(1, client2.getMessages()::size);
        assertEquals(client2.getMessages().get(0), msg);
        timedAssertEquals(1, client1.getMessages()::size);
        assertEquals(client1.getMessages().get(0), msg);
    }

    @Test
    void shouldReceiveOnlyWhenSubscribed(){
        String newTopic = "/topic/channel/def";
        client2.subscribe(newTopic);
        client1.sendMessage("/app/channel/def", "Test");
        timedAssertEquals(1, client2.getMessages()::size);
        timedAssertEquals(0, client1.getMessages()::size);
    }
}
