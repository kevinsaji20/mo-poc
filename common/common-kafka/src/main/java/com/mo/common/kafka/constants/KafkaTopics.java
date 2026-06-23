package com.mo.common.kafka.constants;

public final class KafkaTopics {
    private KafkaTopics() {}

    public static final String CONTENT_PUBLISHED = "content-published";
    public static final String CONTENT_ARCHIVED = "content-archived";
    public static final String RAW_ENGAGEMENT_EVENTS = "raw_engagement_events";
    public static final String COMPUTED_METRICS_EVENTS = "computed_metrics_event";
}
