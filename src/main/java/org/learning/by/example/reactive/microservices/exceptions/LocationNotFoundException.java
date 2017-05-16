package org.learning.by.example.reactive.microservices.exceptions;

public class LocationNotFoundException extends Exception{
    public LocationNotFoundException(final String message) {
        super(message);
    }
}
