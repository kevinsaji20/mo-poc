package com.mo.query_service.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;

@Component
public class CacheTTLStrategy {
    @Value("${spring.data.redis.env.trends-ttl}")
    private Integer TRENDING_TTL;

    @Value("${spring.data.redis.env.metrics-ttl}")
    private Integer METRICS_TTL;

    @Value("${spring.data.redis.env.historical-metrics-ttl}")
    private Integer HISTORICAL_METRICS_TTL;

    public Duration trendingTtl() {
        return Duration.ofMinutes(TRENDING_TTL);
    }

    public Duration contentMetricsTtl(OffsetDateTime to) {
        OffsetDateTime historicalCutOff  = OffsetDateTime.now().minusHours(24);

        if(to.isBefore(historicalCutOff)) {
            return Duration.ofMinutes(HISTORICAL_METRICS_TTL);
        }
        return Duration.ofMinutes(METRICS_TTL);
    }
}
