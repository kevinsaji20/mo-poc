package com.mo.catalog_service.exception;

public class ContentNotFoundException extends RuntimeException {
    public ContentNotFoundException() {
        super("Content Not Found");
    }
}
