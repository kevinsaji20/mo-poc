package com.mo.common.web.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ErrorResponse {
    private boolean success;
    private String message;
    private String errorCode;
    private OffsetDateTime timestamp;
}
