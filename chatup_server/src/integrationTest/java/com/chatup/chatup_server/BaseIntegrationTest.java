package com.chatup.chatup_server;

import com.chatup.chatup_server.client.ClientConfig;
import com.chatup.chatup_server.client.SocketClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.io.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@ActiveProfiles("integration")
//@Testcontainers
@SpringBootTest(classes = {ClientConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @Autowired
    protected SocketClientFactory socketClientFactory;

    @Autowired
    protected RestTemplate restTemplate;


    @Value("${local.server.port}")
    protected int PORT;

    public static final DockerComposeContainer environment;
    private static final Logger elasticLogger = LoggerFactory.getLogger("Elastic logger");
    private static final Logger posgresLogger = LoggerFactory.getLogger("Postgresql logger");
    private static final Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);
    static{
        environment = new DockerComposeContainer(new File("src/integrationTest/resources/compose-test.yml"))
                .withExposedService("postgres", 5432, Wait.forListeningPort())
                .withExposedService("elasticsearch", 9200, Wait.forHttp("/_cluster/health").forStatusCode(200).withStartupTimeout(Duration.of(1, ChronoUnit.MINUTES)))
                .withLogConsumer("elasticsearch", new Slf4jLogConsumer(elasticLogger))
                .withLogConsumer("postgres", new Slf4jLogConsumer(posgresLogger))
                .withStartupTimeout(Duration.of(1, ChronoUnit.MINUTES))
                .withLocalCompose(true)
                .withOptions("--compatibility");
        environment.start();
        Integer port = environment.getServicePort("elasticsearch", 9200);
        String host = environment.getServiceHost("elasticsearch", 9200);
        environment.getContainerByServiceName("elasticsearch").get();

        String httpAddress = host+":"+port;
        logger.info("Address: {}", httpAddress);
        System.getProperties().setProperty("hibernate.search.backend.hosts", httpAddress);
        System.getProperties().setProperty("spring.jpa.properties.hibernate.search.backend.hosts", httpAddress);
    }

    protected <T> void timedAssertEquals(T expected, Supplier<T> actual) {
        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> assertEquals(expected, actual.get()));
    }

    protected HttpHeaders createAuthHeaders(String token) {
        return new HttpHeaders() {{
            set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }};
    }



}
