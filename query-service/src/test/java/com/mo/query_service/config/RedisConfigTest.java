package com.mo.query_service.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.assertj.core.api.Assertions.assertThat;

class RedisConfigTest {

    @Test
    void stringRedisTemplate_setsStringSerializers() {
        RedisConfig config = new RedisConfig();
        RedisConnectionFactory factory = Mockito.mock(RedisConnectionFactory.class);

        StringRedisTemplate template = config.stringRedisTemplate(factory);

        assertThat(template.getConnectionFactory()).isSameAs(factory);
        assertThat(template.getKeySerializer()).isInstanceOf(StringRedisSerializer.class);
        assertThat(template.getValueSerializer()).isInstanceOf(StringRedisSerializer.class);
    }
}
