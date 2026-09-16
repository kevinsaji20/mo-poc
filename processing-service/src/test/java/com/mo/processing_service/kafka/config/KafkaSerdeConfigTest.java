package com.mo.processing_service.kafka.config;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerde;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaSerdeConfigTest {

    @Test
    void createdSerdes_areNotNull() {
        KafkaSerdeConfig config = new KafkaSerdeConfig();

        assertThat(config.watchTimeSessionStateSerde()).isInstanceOf(JsonSerde.class);
        assertThat(config.watchTimeAggregateSerde()).isInstanceOf(JsonSerde.class);
        assertThat(config.completionAggregateSerde()).isInstanceOf(JsonSerde.class);
        assertThat(config.dropoffAggregateSerde()).isInstanceOf(JsonSerde.class);
        assertThat(config.concurrentViewerStateSerde()).isInstanceOf(JsonSerde.class);
        assertThat(config.concurrentViewerAggregateSerde()).isInstanceOf(JsonSerde.class);
    }
}
