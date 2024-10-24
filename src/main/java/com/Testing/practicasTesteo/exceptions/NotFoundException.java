package com.Testing.practicasTesteo.exceptions;

import org.springframework.dao.DataAccessException;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {

        super(message);
    }
}
