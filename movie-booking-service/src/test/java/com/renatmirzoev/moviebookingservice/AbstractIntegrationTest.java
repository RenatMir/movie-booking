package com.renatmirzoev.moviebookingservice;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Execution(ExecutionMode.SAME_THREAD)
public abstract class AbstractIntegrationTest {

    private static final TestEnvironment testEnvironment = TestEnvironment.getInstance();

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", testEnvironment::postgresUrl);
        registry.add("spring.data.redis.port", testEnvironment::redisPort);
        registry.add("spring.data.redis.host", testEnvironment::redisHost);
    }

}
