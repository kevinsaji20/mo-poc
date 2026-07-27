package com.mo.ingestion_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.ttl.duaration}")
    private static long DURATION_CONST;

    private static Duration TTL = Duration.ofHours(DURATION_CONST);

    public boolean isDuplicate(UUID eventId) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(
                        "event" + eventId,
                        "1",
                        TTL
                );
        return Boolean.FALSE.equals(success);
    }
}
