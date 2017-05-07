package org.learning.by.example.reactive.microservices.exceptions;

public class GetQuoteException extends Exception{
    public GetQuoteException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public GetQuoteException(final String message) {
        super(message);
    }
}
