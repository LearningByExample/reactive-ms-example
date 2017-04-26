package org.learning.by.example.reactive.microservices.exceptions;

public class InvalidParametersException extends Exception{
    public InvalidParametersException(String message){
        super(message);
    }
}
