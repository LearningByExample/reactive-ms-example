package org.learning.by.example.reactive.microservices;

public class PathNotFoundException extends Exception{
    PathNotFoundException(String message){
        super(message);
    }
}
