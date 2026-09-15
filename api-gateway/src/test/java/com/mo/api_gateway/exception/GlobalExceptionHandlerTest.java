package com.mo.api_gateway.exception;

import com.mo.common.web.enums.ErrorCode;
import com.mo.common.web.exception.BaseException;
import com.mo.common.web.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBaseException_shouldReturnConfiguredStatusAndBody() {
        BaseException ex = new BaseException("Bad","BAD_ERR",409);

        Mono<ResponseEntity<ErrorResponse>> mono = handler.handleBaseException(ex);
        ResponseEntity<ErrorResponse> resp = mono.block();

        assertThat(resp.getStatusCodeValue()).isEqualTo(409);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("Bad");
        assertThat(resp.getBody().getErrorCode()).isEqualTo("BAD_ERR");
    }

    @Test
    void handleValidationException_shouldReturnBadRequest_withFirstFieldError() {
        WebExchangeBindException webe = Mockito.mock(WebExchangeBindException.class);
        FieldError fe = new FieldError("obj","field","must not be blank");
        when(webe.getFieldErrors()).thenReturn(List.of(fe));

        var resp = handler.handleValidationException(webe).block();

        assertThat(resp.getStatusCodeValue()).isEqualTo(400);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("must not be blank");
        assertThat(resp.getBody().getErrorCode()).isEqualTo(ErrorCode.VALIDATION_ERROR.name());
    }

    @Test
    void handleException_shouldReturnInternalServerError() {
        Exception e = new Exception("boom");
        var resp = handler.handleException(e).block();

        assertThat(resp.getStatusCodeValue()).isEqualTo(500);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("Internal server error");
        assertThat(resp.getBody().getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.name());
    }
}
