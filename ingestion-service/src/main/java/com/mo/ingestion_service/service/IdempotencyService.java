package com.mo.ingestion_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.data.redis.ttl.duration}")
    private long DURATION_CONST;

    public boolean isDuplicate(UUID eventId) {
            Duration TTL = Duration.ofHours(DURATION_CONST);
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(
                            "event:" + eventId,
                            "1",
                            TTL
                    );
            return Boolean.FALSE.equals(success);
    }
}
