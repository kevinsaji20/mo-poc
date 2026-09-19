package com.mo.common.web.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final String errorCode;
    private final int status;

    public BaseException(String message, String errorCode, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}