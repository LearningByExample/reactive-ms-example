package org.learning.by.example.reactive.microservices.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WrongRequest {

    private final String surname;

    @JsonCreator
    public WrongRequest(@JsonProperty("surname") final String surname) {
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }
}
