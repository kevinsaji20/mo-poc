package com.mo.query_service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsCacheService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> T get(String key, Class<T> responseType) {
        try {
            String value = redisTemplate.opsForValue().get(key);

            if (value == null) {
                return null;
            }

            return objectMapper.readValue(value, responseType);
        } catch (JsonProcessingException exception) {
            log.error("Failed to deserialize Redis cache value. key={}", key, exception);
            redisTemplate.delete(key);
            return null;
        }
    }

    public <T> T get(String key, TypeReference<T> typeReference) {
        try {
            String value = redisTemplate.opsForValue().get(key);

            if (value == null) {
                return null;
            }

            return objectMapper.readValue(value, typeReference);
        } catch (JsonProcessingException exception) {
            log.error("Failed to deserialize Redis cache value. key={}", key, exception);
            redisTemplate.delete(key);
            return null;
        }
    }

    public void put(String key, Object value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttl);
        } catch (JsonProcessingException exception) {
            log.error("Failed to serialize cache value. key={}", key, exception);
        }
    }

    public void evict(String key) {
        redisTemplate.delete(key);
    }

    public void evictContent(UUID contentId) {
        String pattern = MetricsCacheKeys.contentPattern(contentId);
        List<String> keys = new ArrayList<>();
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
        } catch (Exception exception) {
            log.error("Failed to scan Redis keys for contentId={}", contentId, exception);
            throw exception;
        }

        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Invalidated {} Redis keys for contentId={}", keys.size(), contentId);
        }

    }
}
