package com.Testing.practicasTesteo.exceptions;

public class PizzeriaNotFoundException extends RuntimeException {
    public PizzeriaNotFoundException(String message) {
        super(message);
    }
}