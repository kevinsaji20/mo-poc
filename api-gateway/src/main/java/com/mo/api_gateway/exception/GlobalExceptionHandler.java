package com.mo.api_gateway.exception;

import com.mo.common.web.enums.ErrorCode;
import com.mo.common.web.exception.BaseException;
import com.mo.common.web.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBaseException(
            BaseException exception
    ) {

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .errorCode(exception.getErrorCode())
                .timestamp(OffsetDateTime.now())
                .build();

        return Mono.just(
                ResponseEntity
                        .status(exception.getStatus())
                        .body(response)
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(
            WebExchangeBindException exception
    ) {

        String message = exception.getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");

        // Log validation failures
        log.warn("Validation failed: {}", message);

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode(ErrorCode.VALIDATION_ERROR.name())
                .timestamp(OffsetDateTime.now())
                .build();

        return Mono.just(
                ResponseEntity.badRequest().body(response)
        );
    }

    @ExceptionHandler({
            ServerWebInputException.class,
            HttpMessageNotReadableException.class
    })
    public Mono<ResponseEntity<Object>> handleBadRequest(Exception ex) {

        String message = ex.getMessage() != null
                ? ex.getMessage()
                : "Invalid request";

        // Log bad request details
        log.warn("Bad request: {}", message);

        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", message))
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleException(
            Exception exception
    ) {
        // Log unexpected exceptions for diagnostics
        log.error("Unhandled exception caught in GlobalExceptionHandler", exception);

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message("Internal server error")
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR.name())
                .timestamp(OffsetDateTime.now())
                .build();

        return Mono.just(
                ResponseEntity.status(500).body(response)
        );
    }
}