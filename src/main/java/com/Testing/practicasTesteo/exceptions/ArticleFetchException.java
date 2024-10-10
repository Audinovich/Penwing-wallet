package com.Testing.practicasTesteo.exceptions;

public class ArticleFetchException extends Exception {
    public ArticleFetchException(String message) {
        super(message);
    }

    public ArticleFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}