package com.chatup.chatup_server;

import com.chatup.chatup_server.client.ClientConfig;
import com.chatup.chatup_server.client.SocketClientFactory;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
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
    public static final PostgreSQLContainer postgreContainer;
    @ClassRule
    public static final ElasticsearchContainer elasticContainer;

    @ClassRule
    public static final RabbitMQContainer rabbitContainer;

    private static final Logger elasticLogger = LoggerFactory.getLogger(ElasticsearchContainer.class);
    private static final Logger posgresLogger = LoggerFactory.getLogger(PostgreSQLContainer.class);
    private static final Logger rabbitLogger = LoggerFactory.getLogger(RabbitMQContainer.class);

    static {
        postgreContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres")
                .withLogConsumer(new Slf4jLogConsumer(posgresLogger));
        elasticContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.5.3")
                .withEnv("xpack.security.enabled", "false")
                .withEnv("discovery.type", "single-node")
                .withEnv("ES_JAVA_OPTS", "-Xms1g -Xmx1g")
                .withEnv("cluster.routing.allocation.disk.threshold_enabled", "true")
                .withEnv("cluster.routing.allocation.disk.watermark.flood_stage", "200mb")
                .withEnv("cluster.routing.allocation.disk.watermark.low", "500mb")
                .withEnv("cluster.routing.allocation.disk.watermark.high", "300mb")
                .withEnv("bootstrap.memory_lock", "true")
                .withLogConsumer(new Slf4jLogConsumer(elasticLogger))
                .withExposedPorts(9200)
                .waitingFor(Wait
                        .forHttp("/_cluster/health")
                        .forStatusCode(200)
                        .withStartupTimeout(Duration.of(1, ChronoUnit.MINUTES)));
        rabbitContainer = new RabbitMQContainer("rabbitmq:3.9-management")
                .withExposedPorts(5672, 61613, 15672)
                .withLogConsumer(new Slf4jLogConsumer(rabbitLogger))
                .withPluginsEnabled("rabbitmq_management", "rabbitmq_management_agent", "rabbitmq_stomp", "rabbitmq_web_dispatch")
                .waitingFor(Wait.forHttp("/api/vhosts")
                        .forPort(15672)
                        .withBasicCredentials("guest", "guest"));
        rabbitContainer.start();
        postgreContainer.start();
        elasticContainer.start();
        System.getProperties().setProperty("hibernate.search.backend.hosts", elasticContainer.getHttpHostAddress());
        System.getProperties().setProperty("spring.jpa.properties.hibernate.search.backend.hosts", elasticContainer.getHttpHostAddress());
        System.getProperties().setProperty("spring.rabbitmq.host", rabbitContainer.getHost());
        System.getProperties().setProperty("spring.rabbitmq.port", String.valueOf(rabbitContainer.getMappedPort(5672)));
        System.getProperties().setProperty("spring.rabbitmq.relay-port", String.valueOf(rabbitContainer.getMappedPort(61613)));
        System.getProperties().setProperty("spring.rabbitmq.relay-host", rabbitContainer.getHost());
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
