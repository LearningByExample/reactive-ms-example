package org.learning.by.example.reactive.microservices.model;

public class HelloResponse {

    private String hello;

    public HelloResponse() {
        this.hello = "";
    }

    public HelloResponse(final String hello) {
        this.hello = hello;
    }

    public String getHello() {
        return hello;
    }

    public void setHello(final String hello) {
        this.hello = hello;
    }
}
