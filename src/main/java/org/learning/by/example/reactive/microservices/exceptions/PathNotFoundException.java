package org.learning.by.example.reactive.microservices.exceptions;

public class PathNotFoundException extends Exception{
    public PathNotFoundException(String message){
        super(message);
    }
}
