package com.mo.ingestion_service.exception;

import com.mo.common.web.enums.ErrorCode;
import com.mo.common.web.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RateLimitException extends BaseException {
    public RateLimitException() {
        super(
                "Too Many Requests",
                ErrorCode.TOO_MANY_REQUESTS.name(),
                HttpStatus.TOO_MANY_REQUESTS.value()
        );
    }
}
