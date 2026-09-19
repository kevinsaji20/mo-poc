package com.mo.common.web.exception;

import com.mo.common.web.enums.ErrorCode;
import com.mo.common.web.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBaseException_returnsMappedErrorResponse() {
        BaseException exception = new BaseException("Content not found", "RESOURCE_NOT_FOUND", 404);

        var response = handler.handleBaseException(exception);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Content not found");
        assertThat(response.getBody().getErrorCode()).isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    void handleValidation_returnsBadRequestResponse() {
        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(ex.getBindingResult()).thenReturn(bindingResult);
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(
                new FieldError("obj", "title", "Title is required")
        ));

        var response = handler.handleValidation(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Title is required");
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.VALIDATION_ERROR.name());
    }

    @Test
    void handleException_returnsInternalServerErrorResponse() {
        var response = handler.handleException(new RuntimeException("Boom"));

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Internal server error");
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.name());
    }
}
