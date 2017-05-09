package org.learning.by.example.reactive.microservices.model;

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
