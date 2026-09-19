package com.mo.query_service.cache;

import com.mo.query_service.dto.request.MetricsQueryRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

public class MetricsCacheKeys {
    private static final String PREFIX = "metrics";

    private MetricsCacheKeys() {}

    public static String summary(UUID contentId, MetricsQueryRequest query) {
        return PREFIX + ":" + contentId + ":" + "summary:" +
                query.from() + ":" + query.to();
    }

    public static String watchTime(UUID contentId, MetricsQueryRequest query) {
        return PREFIX + ":" + contentId + ":" + "watch-time:" +
                query.granularity() + ":" + query.from() + ":" + query.to();
    }

    public static String completion(UUID contentId, MetricsQueryRequest query) {
        return PREFIX + ":" + contentId + ":" + "completion:" +
                query.granularity() + ":" + query.from() + ":" + query.to();
    }

    public static String dropoff(UUID contentId, MetricsQueryRequest query) {
        return PREFIX + ":" + contentId + ":" + "dropoff:" +
                query.from() + ":" + query.to();
    }

    public static String concurrent(UUID contentId, MetricsQueryRequest query) {
        return PREFIX + ":" + contentId + ":" + "concurrent:" +
                query.granularity() + ":" + query.from() + ":" + query.to();
    }

    public static String topContent(MetricsQueryRequest query) {
        return PREFIX + ":" + "top-content:" + query.from() + ":" +
                query.to() + ":" + query.size() + ":" + query.offset();
    }

    public static String mostCompleted(MetricsQueryRequest query) {
        return PREFIX + ":" + "most-completed:" + query.from() + ":" +
                query.to() + ":" + query.size() + ":" + query.offset();
    }

    public static String topGenreContent(String genre, MetricsQueryRequest query) {
        return PREFIX + ":" + "top-genre-content:" +
                genre + ":" + query.from() + ":" +
                query.to() + ":" + query.size() + ":" + query.offset();
    }

    public static String contentPattern(UUID contentId) {
        return PREFIX + ":" + contentId + ":*";
    }
}
