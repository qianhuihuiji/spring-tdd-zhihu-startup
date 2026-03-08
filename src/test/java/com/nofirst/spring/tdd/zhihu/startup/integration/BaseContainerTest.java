package com.nofirst.spring.tdd.zhihu.startup.integration;

import com.nofirst.spring.tdd.zhihu.startup.SpringTddZhihuStartupApplication;
import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.TestcontainersConfiguration;

@SpringBootTest(classes = SpringTddZhihuStartupApplication.class)
public abstract class BaseContainerTest {

    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("zhihu")
            .withUsername("root")
            .withPassword("root")
            .withReuse(true);

    public static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
    ).withReuse(true);

    public static final RedisContainer redisContainer =
            new RedisContainer(DockerImageName.parse("redis:7.2.3"))
                    .withExposedPorts(6379)
                    .withReuse(true);

    static {
        // 复用配置
        TestcontainersConfiguration.getInstance().updateUserConfig("testcontainers.reuse.enable", "true");
        // 启动容器，确保端口提前分配
        mysqlContainer.start();
        kafkaContainer.start();
        redisContainer.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        // 将 Testcontainers 启动的 Kafka 容器的实际连接地址，动态覆盖到 Spring 上下文的 spring.kafka.bootstrap-servers 配置项中
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }
}
