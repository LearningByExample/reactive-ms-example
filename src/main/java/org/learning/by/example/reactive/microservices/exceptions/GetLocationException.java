package org.learning.by.example.reactive.microservices.exceptions;

public class GetLocationException extends Exception{
    public GetLocationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public GetLocationException(final String message) {
        super(message);
    }
}
