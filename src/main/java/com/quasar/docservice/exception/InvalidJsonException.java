package com.quasar.docservice.exception;

public class InvalidJsonException extends RuntimeException {
    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
