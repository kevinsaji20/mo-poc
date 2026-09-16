package com.mo.query_service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsCacheServiceTest {

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks private MetricsCacheService metricsCacheService;

    @Test
    void get_whenValueExists_deserializesJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        metricsCacheService = new MetricsCacheService(redisTemplate, mapper);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("alpha")).thenReturn("\"beta\"");

        String actual = metricsCacheService.get("alpha", String.class);

        assertThat(actual).isEqualTo("beta");
    }

    @Test
    void put_serializesObjectAndSetsTtl() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        metricsCacheService = new MetricsCacheService(redisTemplate, mapper);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        metricsCacheService.put("demo", "hello", Duration.ofMinutes(5));

        verify(valueOperations).set(eq("demo"), eq("\"hello\""), eq(Duration.ofMinutes(5)));
    }

    @Test
    void evict_deletesKey() {
        when(redisTemplate.delete("demo")).thenReturn(true);

        metricsCacheService.evict("demo");

        verify(redisTemplate).delete("demo");
    }
}
