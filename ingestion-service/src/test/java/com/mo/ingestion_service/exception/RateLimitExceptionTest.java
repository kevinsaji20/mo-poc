package com.mo.ingestion_service.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitExceptionTest {

    @Test
    void constructor_setsExpectedMessageCodeAndStatus() {
        RateLimitException exception = new RateLimitException();

        assertThat(exception.getMessage()).isEqualTo("Too Many Requests");
        assertThat(exception.getErrorCode()).isEqualTo("TOO_MANY_REQUESTS");
        assertThat(exception.getStatus()).isEqualTo(429);
    }
}
