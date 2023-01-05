package com.chatup.chatup_server.web;

import com.chatup.chatup_server.BaseIntegrationTest;
import com.chatup.chatup_server.client.SocketClient;
import com.chatup.chatup_server.repository.MessageRepository;
import com.chatup.chatup_server.service.messaging.OutgoingMessage;
import com.chatup.chatup_server.service.utils.InstantService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Sql({"file:src/integrationTest/resources/cleanUp.sql", "file:src/integrationTest/resources/init.sql"})
public class ChatTest extends BaseIntegrationTest {

    @Autowired
    private SimpUserRegistry simpUserRegistry;
    @MockBean
    private InstantService instantService;
    @Autowired
    private MessageRepository messageRepository;

    SocketClient client1;
    SocketClient client2;
    private static final Long CHANNEL = 123L;
    private static final String USER_1 = "test.test.1";
    private static final String USER_2 = "test.test.2";
    private static final String USER_3 = "test.test.3";
    private static final String USER_4 = "test.test.4";
    private static final LinkedList<String> topics = new LinkedList<>() {{
        add("/topic/channel." + CHANNEL);
    }};

    private static final String BROADCAST_ENDPOINT = "/app/channel." + CHANNEL;

    @BeforeEach
    void initClient() {
        client1 = socketClientFactory.getClient(USER_1, topics);
        client2 = socketClientFactory.getClient(USER_2, topics);
    }

    @AfterEach
    void closeConnections() {
        client1.close();
        client2.close();
    }


    @Test
    void shouldManageConnections() {
        //Given
        int userCount = simpUserRegistry.getUserCount();

        //When
        SocketClient client1 = socketClientFactory.getClient(USER_3, topics);
        SocketClient client2 = socketClientFactory.getClient(USER_4, topics);

        //Then
        timedAssertEquals(userCount+2, simpUserRegistry::getUserCount);

        //When
        client1.close();
        client2.close();

        //Then
        timedAssertEquals(userCount, simpUserRegistry::getUserCount);

    }

    @Test
    void shouldBroadcastAndPreserveMessages() {
        //Given
        String msg = "Test";
        Instant time = Instant.ofEpochSecond(2137420L);
        when(instantService.getNow()).thenReturn(time);
        long messageCount = messageRepository.count();

        //When
        client1.sendMessage(BROADCAST_ENDPOINT, msg);

        //Then
        timedAssertEquals(1, client2.getMessages()::size);
        OutgoingMessage message = client2.getMessages().get(0);
        assertEquals(msg, message.content());
        assertEquals(USER_1, message.authorUsername());
        assertEquals(time, message.timeCreated());
        assertEquals(CHANNEL, message.channelID());

        timedAssertEquals(1, client1.getMessages()::size);
        message = client1.getMessages().get(0);
        assertEquals(msg, message.content());
        assertEquals(USER_1, message.authorUsername());
        assertEquals(time, message.timeCreated());
        assertEquals(CHANNEL, message.channelID());
        timedAssertEquals(1, client1.getMessages()::size);

        assertEquals(messageCount+1, messageRepository.count());
    }

    @Test
    void shouldReceiveOnlyWhenSubscribed() {
        //Given
        String newTopic = "/topic/channel.345";

        //When
        client2.subscribe(newTopic);
        client1.sendMessage("/app/channel.345", "Test");

        //Then
        timedAssertEquals(1, client2.getMessages()::size);
        timedAssertEquals(0, client1.getMessages()::size);
    }
}
