package com.mo.processing_service.kafka.producer;

import com.mo.common.kafka.constants.KafkaTopics;
import com.mo.common.kafka.enums.EventType;
import com.mo.common.kafka.enums.MetricType;
import com.mo.common.kafka.envelope.EventEnvelope;
import com.mo.common.kafka.events.ComputedMetricEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComputedMetricsProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private ComputedMetricsProducer producer;

    @Test
    void publishComputedMetricsEvents_sendsEnvelopeToKafka() {
        UUID contentId = UUID.randomUUID();
        ComputedMetricEvent metricEvent = new ComputedMetricEvent(
                contentId,
                MetricType.WATCH_TIME,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                123L,
                OffsetDateTime.now()
        );
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

        producer.publishComputedMetricsEvents(metricEvent);

        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(eq(KafkaTopics.COMPUTED_METRICS_EVENTS), anyString(), payloadCaptor.capture());

        Object payload = payloadCaptor.getValue();
        assertThat(payload).isInstanceOf(EventEnvelope.class);
        EventEnvelope<?> envelope = (EventEnvelope<?>) payload;
        assertThat(envelope.eventType()).isEqualTo(EventType.COMPUTED_METRICS_EVENTS);
        assertThat(envelope.payload()).isEqualTo(metricEvent);
    }
}
