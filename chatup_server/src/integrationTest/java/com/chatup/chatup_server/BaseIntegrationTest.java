package com.chatup.chatup_server;

import com.chatup.chatup_server.client.ClientConfig;
import com.chatup.chatup_server.client.SocketClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(classes = {ClientConfig.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @Autowired
    protected SocketClientFactory socketClientFactory;



    protected <T> void timedAssertEquals(T expected, Supplier<T> actual){
        await()
                .atMost(1, TimeUnit.SECONDS)
                .untilAsserted(()->assertEquals(expected, actual.get()));
    }
}
