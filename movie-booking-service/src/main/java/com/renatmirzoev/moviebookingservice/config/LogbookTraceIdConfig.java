package com.renatmirzoev.moviebookingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.CorrelationId;

import java.util.UUID;

@Configuration
public class LogbookTraceIdConfig {

    @Bean
    CorrelationId traceId() {
        return r -> UUID.randomUUID().toString();
    }
}
