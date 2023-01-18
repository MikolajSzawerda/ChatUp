package com.chatup.chatup_server.web;

import com.chatup.chatup_server.BaseInitializedDbTest;
import com.chatup.chatup_server.client.SocketClient;
import com.chatup.chatup_server.client.SocketClientFactory;
import com.chatup.chatup_server.repository.MessageRepository;
import com.chatup.chatup_server.service.messaging.OutgoingMessage;
import com.chatup.chatup_server.service.utils.InstantService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Sql({"file:src/integrationTest/resources/cleanUp.sql", "file:src/integrationTest/resources/init.sql"})
public class ChatTest extends BaseInitializedDbTest {

    @Autowired
    private SimpUserRegistry simpUserRegistry;
    @MockBean
    private InstantService instantService;
    @Autowired
    private MessageRepository messageRepository;

    @Test
    void shouldManageConnections() {
        //Given
        int userCount = simpUserRegistry.getUserCount();

        //When
        SocketClient client1 = socketClientFactory.getClient(USER_1);
        SocketClient client2 = socketClientFactory.getClient(USER_2);

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
        Long channel = 3L;
        Instant time = Instant.ofEpochSecond(2137420L);
        when(instantService.getNow()).thenReturn(time);
        long messageCount = messageRepository.count();
        SocketClient client1 = socketClientFactory.getClient(USER_1);
        SocketClient client2 = socketClientFactory.getClient(USER_2);

        //When
        client1.sendMessage("/app/"+channel, msg);

        //Then
        timedAssertEquals(1, client2.getMessages()::size);
        OutgoingMessage message = client2.getMessages().get(0);
        assertEquals(msg, message.content());
        assertEquals(USER_1, message.authorUsername());
        assertEquals(time, message.timeCreated());
        assertEquals(channel, message.channelID());

        timedAssertEquals(1, client1.getMessages()::size);
        message = client1.getMessages().get(0);
        assertEquals(msg, message.content());
        assertEquals(USER_1, message.authorUsername());
        assertEquals(time, message.timeCreated());
        assertEquals(channel, message.channelID());
        timedAssertEquals(1, client1.getMessages()::size);

        assertEquals(messageCount+1, messageRepository.count());

        client1.close();
        client2.close();
    }

    @Test
    void shouldReceiveOnlyWhenSubscribed() {
        //Given
        Long id  = addNewChannel(createUserToken(USER_2), USER_2, USER_1);
        SocketClient client1 = socketClientFactory.getClient(USER_1);
        SocketClient client2 = socketClientFactory.getClient(USER_2);
        SocketClient client3 = socketClientFactory.getClient(USER_3);
        SocketClient client4 = socketClientFactory.getClient(USER_4);

        //When
        client1.sendMessage("/app/"+id, "Test");

        //Then
        timedAssertEquals(1, client1.getMessages()::size);
        timedAssertEquals(1, client2.getMessages()::size);
        timedAssertEquals(0, client3.getMessages()::size);
        timedAssertEquals(0, client4.getMessages()::size);

        client1.close();
        client2.close();
        client3.close();
        client4.close();
    }


}
