package com.mo.catalog_service.kafka.config;

import com.mo.common.kafka.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConfigTest {

    @Test
    void createsTopicsWithExpectedNamesAndPartitions() {
        KafkaConfig cfg = new KafkaConfig();

        NewTopic published = cfg.contentPublishedTopic();
        NewTopic archived = cfg.contentArchivedTopic();

        assertThat(published).isNotNull();
        assertThat(published.name()).isEqualTo(KafkaTopics.CONTENT_PUBLISHED);

        assertThat(archived).isNotNull();
        assertThat(archived.name()).isEqualTo(KafkaTopics.CONTENT_ARCHIVED);
    }
}
