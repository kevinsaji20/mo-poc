package com.mo.ingestion_service.service;

import com.mo.ingestion_service.exception.RateLimitException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RateLimitService {
    @Value("${spring.rate.limit}")
    private static long LIMIT;

    @Value("${spring.rate.limit.window.seconds}")
    private static long WINDOW_SECONDS ;

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> rateLimitScript;

    public void validate(UUID userId) {
        String key = "rate:user:" + userId;

        Long count = redisTemplate.execute(
                rateLimitScript,
                Collections.singletonList(key),
                String.valueOf(WINDOW_SECONDS)
        );

        if (count > LIMIT) {
            throw new RateLimitException();
        }
    }
}
