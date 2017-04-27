package org.learning.by.example.reactive.microservices.model;

/**
 * Created by mail on 23/04/2017.
 */
public class WrongRequest {
    private String surname;

    public WrongRequest() {
        this.surname = "";
    }
    public WrongRequest(final String surname) {
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(final String surname) {
        this.surname = surname;
    }
}
