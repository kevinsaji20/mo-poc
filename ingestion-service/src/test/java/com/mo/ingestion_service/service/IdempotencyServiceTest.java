package com.mo.ingestion_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private IdempotencyService idempotencyService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(IdempotencyService.class, "TTL", Duration.ofHours(1));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        idempotencyService = new IdempotencyService(redisTemplate);
    }

    @Test
    void isDuplicate_whenKeyAlreadyExists_returnsTrue() {
        UUID eventId = UUID.randomUUID();
        when(valueOperations.setIfAbsent(eq("event" + eventId), eq("1"), eq(Duration.ofHours(1))))
                .thenReturn(Boolean.FALSE);

        assertThat(idempotencyService.isDuplicate(eventId)).isTrue();
    }

    @Test
    void isDuplicate_whenKeyDoesNotExist_returnsFalse() {
        UUID eventId = UUID.randomUUID();
        when(valueOperations.setIfAbsent(eq("event" + eventId), eq("1"), eq(Duration.ofHours(1))))
                .thenReturn(Boolean.TRUE);

        assertThat(idempotencyService.isDuplicate(eventId)).isFalse();
    }
}
