package com.mo.processing_service.kafka.serde;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.common.kafka.envelope.EventEnvelope;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

@RequiredArgsConstructor
public class EventEnvelopeSerde<T> implements Serde<EventEnvelope<T>> {
    private final ObjectMapper objectMapper;
    private final Class<T> payloadType;

    @Override
    public Serializer<EventEnvelope<T>> serializer() {
        return (topic, data) -> {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new IllegalStateException(
                        "Failed to serialize EventEnvelope",
                        e
                );
            }
        };
    }

    @Override
    public Deserializer<EventEnvelope<T>> deserializer() {
        return (topic, data) -> {
            if (data == null) {
                return null;
            }
            try {
                JavaType type = objectMapper.getTypeFactory()
                        .constructParametricType(
                                EventEnvelope.class,
                                payloadType
                        );

                return objectMapper.readValue(data, type);
            } catch (Exception e) {
                throw new IllegalStateException(
                        "Failed to deserialize EventEnvelope",
                        e
                );
            }
        };
    }
}
