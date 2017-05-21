package org.learning.by.example.reactive.microservices.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {

    private final String error;

    @JsonCreator
    public ErrorResponse(@JsonProperty("error") final String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
