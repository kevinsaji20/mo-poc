package com.mo.catalog_service.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentNotFoundExceptionTest {

    @Test
    void exception_containsMessage_and_status() {
        ContentNotFoundException ex = new ContentNotFoundException();
        assertThat(ex.getMessage()).isEqualTo("Content not found");
        assertEquals(404, ex.getStatus());
        assertThat(ex.getErrorCode()).isEqualTo("RESOURCE_NOT_FOUND");
    }
}
