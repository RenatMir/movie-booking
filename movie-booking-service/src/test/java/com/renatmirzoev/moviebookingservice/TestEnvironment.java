package com.renatmirzoev.moviebookingservice;

import com.redis.testcontainers.RedisContainer;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Stream;

@Slf4j
public class TestEnvironment {

    private static final TestEnvironment INSTANCE = new TestEnvironment();

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER;
    private static final RedisContainer REDIS_CONTAINER;

    static {
        POSTGRES_CONTAINER = createPostgresContainer();
        REDIS_CONTAINER = createRedisContainer();

        Stream.of(POSTGRES_CONTAINER, REDIS_CONTAINER)
            .parallel()
            .forEach(GenericContainer::start);
    }

    private TestEnvironment() {
    }

    public static TestEnvironment getInstance() {
        return INSTANCE;
    }

    public String postgresUrl() {
        return String.format("jdbc:postgresql://%s:%s/movie-booking",
            POSTGRES_CONTAINER.getHost(),
            POSTGRES_CONTAINER.getFirstMappedPort().toString()
        );
    }

    public String redisPort() {
        return REDIS_CONTAINER.getFirstMappedPort().toString();
    }

    public String redisHost() {
        return REDIS_CONTAINER.getHost();
    }

    @SuppressWarnings("resource")
    private static PostgreSQLContainer<?> createPostgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
            .withDatabaseName("movie-booking")
            .withUsername("movie-booking")
            .withPassword("movie-booking")
            .waitingFor(Wait.forListeningPort());
    }

    @SuppressWarnings("resource")
    private static RedisContainer createRedisContainer() {
        return new RedisContainer(DockerImageName.parse("redis:8.0.1"))
            .waitingFor(Wait.forListeningPort());
    }
}
