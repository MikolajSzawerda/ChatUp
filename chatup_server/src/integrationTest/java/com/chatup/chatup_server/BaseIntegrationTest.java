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
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.*;
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
    private static final String TEST_COMPOSE = "version: '3.8'\n" +
            "\n" +
            "services:\n" +
            "  postgres:\n" +
            "    image: postgres\n" +
            "    ports:\n" +
            "      - \"5432:5432\"\n" +
            "    environment:\n" +
            "      POSTGRES_USER: postgres\n" +
            "      POSTGRES_PASSWORD: password\n" +
            "      POSTGRES_DB: chat\n" +
            "  elasticsearch:\n" +
            "    image: docker.elastic.co/elasticsearch/elasticsearch:8.5.3\n" +
            "    environment:\n" +
            "      - xpack.security.enabled=false\n" +
            "      - discovery.type=single-node\n" +
            "      - \"ES_JAVA_OPTS=-Xms1g -Xmx1g\"\n" +
            "      - cluster.routing.allocation.disk.threshold_enabled=true\n" +
            "      - cluster.routing.allocation.disk.watermark.flood_stage=200mb\n" +
            "      - cluster.routing.allocation.disk.watermark.low=500mb\n" +
            "      - cluster.routing.allocation.disk.watermark.high=300mb\n" +
            "      - bootstrap.memory_lock=true\n" +
            "    ports:\n" +
            "      - \"9200:9200\"\n" +
            "    ulimits:\n" +
            "      memlock:\n" +
            "        soft: -1\n" +
            "        hard: -1\n";

    static{
        File compose = new File("test-compose.yml");
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(compose))){
            bufferedWriter.write(TEST_COMPOSE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        environment = new DockerComposeContainer(compose)
                .withExposedService("postgres", 5432, Wait.forListeningPort())
                .withExposedService("elasticsearch", 9200,
                        Wait
                                .forHttp("/_cluster/health")
                                .forStatusCode(200)
                )
                .withLogConsumer("elasticsearch", new Slf4jLogConsumer(elasticLogger))
                .withLogConsumer("postgres", new Slf4jLogConsumer(posgresLogger))
                .withLocalCompose(true)
                .withOptions("--compatibility");
        environment.start();
        elasticLogger.info("Port: "+environment.getServicePort("elasticsearch", 9200));
        elasticLogger.info("Host: "+environment.getServiceHost("elasticsearch", 9200));
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
