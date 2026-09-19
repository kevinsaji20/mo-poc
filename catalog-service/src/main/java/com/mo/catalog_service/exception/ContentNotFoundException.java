package com.mo.catalog_service.exception;

import com.mo.common.web.enums.ErrorCode;
import com.mo.common.web.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ContentNotFoundException extends BaseException {
    public ContentNotFoundException() {
        super(
                "Content not found",
                ErrorCode.RESOURCE_NOT_FOUND.name(),
                HttpStatus.NOT_FOUND.value()
        );
    }
}
