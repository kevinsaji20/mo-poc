package com.mo.processing_service.kafka.config;

import com.mo.common.kafka.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConfigTest {

    @Test
    void computedMetricsEventTopic_hasExpectedNamePartitionsAndReplication() {
        KafkaConfig config = new KafkaConfig();

        NewTopic topic = config.computedMetricsEventTopic();

        assertThat(topic).isNotNull();
        assertThat(topic.name()).isEqualTo(KafkaTopics.COMPUTED_METRICS_EVENTS);
        assertThat(topic.numPartitions()).isEqualTo(3);
        assertThat(topic.replicationFactor()).isEqualTo((short) 1);
    }
}
