package org.learning.by.example.reactive.microservices.model;


public class ErrorResponse {
    private String error;

    public ErrorResponse() {
        this.error = "";
    }

    public ErrorResponse(String hello) {
        this.error = hello;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
