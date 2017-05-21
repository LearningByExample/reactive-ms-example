package org.learning.by.example.reactive.microservices.exceptions;

public class GeoLocationNotFoundException extends Exception{

    public GeoLocationNotFoundException(final String message) {
        super(message);
    }
}
