package com.mo.common.web.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseExceptionTest {

    @Test
    void storesMessageErrorCodeAndStatus() {
        BaseException ex = new BaseException("Not found", "RESOURCE_NOT_FOUND", 404);

        assertThat(ex.getMessage()).isEqualTo("Not found");
        assertThat(ex.getErrorCode()).isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(ex.getStatus()).isEqualTo(404);
    }
}
