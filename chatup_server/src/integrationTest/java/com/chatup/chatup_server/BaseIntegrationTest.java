package com.chatup.chatup_server;

import com.chatup.chatup_server.client.ClientConfig;
import com.chatup.chatup_server.client.SocketClientFactory;
import org.junit.ClassRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

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

    @Autowired
    protected RestTemplate restTemplate;


    @Value("${local.server.port}")
    protected int PORT;


    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1");


    protected <T> void timedAssertEquals(T expected, Supplier<T> actual) {
        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> assertEquals(expected, actual.get()));
    }



    protected HttpHeaders createAuthHeaders(String token){
        return new HttpHeaders(){{
         set(HttpHeaders.AUTHORIZATION, "Bearer "+token);
        }};
    }




}
