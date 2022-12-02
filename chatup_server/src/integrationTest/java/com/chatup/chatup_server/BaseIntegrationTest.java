package com.chatup.chatup_server;

import com.chatup.chatup_server.client.ClientConfig;
import com.chatup.chatup_server.client.SocketClientFactory;
import com.chatup.chatup_server.repository.AppUserRepository;
import com.chatup.chatup_server.service.AuthService;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@ActiveProfiles("integration")
@Testcontainers
@SpringBootTest(classes = {ClientConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @Autowired
    protected SocketClientFactory socketClientFactory;


    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:9.6.8");


    protected <T> void timedAssertEquals(T expected, Supplier<T> actual) {
        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> assertEquals(expected, actual.get()));
    }


}
