package com.Testing.practicasTesteo.exceptions;

public class NotDeletedException extends RuntimeException {
    public NotDeletedException(String message) {
        super(message);
    }

    public NotDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
}