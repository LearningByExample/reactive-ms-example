package org.learning.by.example.reactive.microservices.exceptions;

public class GetGeoLocationException extends Exception {
    public GetGeoLocationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public GetGeoLocationException(final String message) {
        super(message);
    }
}
