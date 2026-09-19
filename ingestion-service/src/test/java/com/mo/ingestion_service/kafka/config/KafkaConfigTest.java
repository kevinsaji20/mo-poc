package com.mo.ingestion_service.kafka.config;

import com.mo.common.kafka.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConfigTest {

    @Test
    void rawEngagementEventsTopic_hasExpectedNamePartitionsAndReplication() {
        KafkaConfig config = new KafkaConfig();

        NewTopic topic = config.rawEngagementEventsTopic();

        assertThat(topic).isNotNull();
        assertThat(topic.name()).isEqualTo(KafkaTopics.RAW_ENGAGEMENT_EVENTS);
        assertThat(topic.numPartitions()).isEqualTo(3);
        assertThat(topic.replicationFactor()).isEqualTo((short) 1);
    }
}
