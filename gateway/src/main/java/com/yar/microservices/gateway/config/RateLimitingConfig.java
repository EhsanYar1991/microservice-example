package com.yar.microservices.gateway.config;

import com.yar.microservices.gateway.ratelimiting.RateLimitingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RateLimitingConfig {

    @Bean
    @Primary
    public RateLimitingFilter rateLimitingFilter() {
        return new RateLimitingFilter();
    }

}
