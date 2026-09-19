package com.mo.ingestion_service.service;

import com.mo.ingestion_service.exception.RateLimitException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private RedisScript<Long> rateLimitScript;

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(RateLimitService.class, "LIMIT", 10L);
        ReflectionTestUtils.setField(RateLimitService.class, "WINDOW_SECONDS", 60L);
        rateLimitService = new RateLimitService(redisTemplate, rateLimitScript);
    }

    @Test
    void validate_whenCountDoesNotExceedLimit_doesNotThrow() {
        UUID userId = UUID.randomUUID();
        when(redisTemplate.execute(any(RedisScript.class), anyList(), eq("60")))
                .thenReturn(9L);

        assertThatCode(() -> rateLimitService.validate(userId)).doesNotThrowAnyException();
    }

    @Test
    void validate_whenCountExceedsLimit_throwsRateLimitException() {
        UUID userId = UUID.randomUUID();
        when(redisTemplate.execute(any(RedisScript.class), anyList(), eq("60")))
                .thenReturn(11L);

        assertThatThrownBy(() -> rateLimitService.validate(userId))
                .isInstanceOf(RateLimitException.class)
                .hasMessage("Too Many Requests");
    }
}
