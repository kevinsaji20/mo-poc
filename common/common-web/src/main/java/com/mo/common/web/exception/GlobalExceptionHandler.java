package com.mo.common.web.exception;

import com.mo.common.web.enums.ErrorCode;
import com.mo.common.web.response.ErrorResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException exception
    ) {

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .errorCode(exception.getErrorCode())
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode(ErrorCode.VALIDATION_ERROR.name())
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex
    ) {

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message("Internal server error")
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR.name())
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.internalServerError().body(response);
    }
}