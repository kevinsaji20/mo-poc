package com.mo.query_service.exception;

import com.mo.common.web.enums.ErrorCode;
import com.mo.common.web.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidMetricQueryException extends BaseException {
    public InvalidMetricQueryException(String exception) {
        super(
                exception,
                ErrorCode.VALIDATION_ERROR.name(),
                HttpStatus.BAD_REQUEST.value()
        );
    }
}
