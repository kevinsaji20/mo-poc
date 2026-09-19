package com.mo.query_service.cache;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CacheTTLStrategyTest {

    @Test
    void trendingTtl_usesConfiguredValue() {
        CacheTTLStrategy strategy = new CacheTTLStrategy();
        ReflectionTestUtils.setField(strategy, "TRENDING_TTL", 15);

        assertThat(strategy.trendingTtl()).isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void contentMetricsTtl_usesHistoricalSettingForOlderRange() {
        CacheTTLStrategy strategy = new CacheTTLStrategy();
        ReflectionTestUtils.setField(strategy, "METRICS_TTL", 30);
        ReflectionTestUtils.setField(strategy, "HISTORICAL_METRICS_TTL", 120);

        OffsetDateTime to = OffsetDateTime.now().minusHours(30);

        assertThat(strategy.contentMetricsTtl(to)).isEqualTo(Duration.ofMinutes(120));
    }

    @Test
    void contentMetricsTtl_usesStandardSettingForRecentRange() {
        CacheTTLStrategy strategy = new CacheTTLStrategy();
        ReflectionTestUtils.setField(strategy, "METRICS_TTL", 30);
        ReflectionTestUtils.setField(strategy, "HISTORICAL_METRICS_TTL", 120);

        OffsetDateTime to = OffsetDateTime.now();

        assertThat(strategy.contentMetricsTtl(to)).isEqualTo(Duration.ofMinutes(30));
    }
}
