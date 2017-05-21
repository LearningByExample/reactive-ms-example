package org.learning.by.example.reactive.microservices.exceptions;

public class GetSunriseSunsetException extends Exception {

    public GetSunriseSunsetException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public GetSunriseSunsetException(final String message) {
        super(message);
    }
}
