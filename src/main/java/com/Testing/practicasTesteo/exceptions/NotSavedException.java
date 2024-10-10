package com.Testing.practicasTesteo.exceptions;

public class NotSavedException extends RuntimeException{

    public NotSavedException(String message){
        super(message);
    }
    public NotSavedException(String message,Throwable cause){
        super(message,cause);
    }
}
