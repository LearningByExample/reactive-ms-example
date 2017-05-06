package org.learning.by.example.reactive.microservices.model;

public class HelloResponse {

    private String hello;
    private String quote;

    public HelloResponse() {
        this.hello = "";
        this.quote = "";
    }

    public HelloResponse(final String hello, final String quote) {
        this.hello = hello;
        this.quote = quote;
    }

    public String getHello() {
        return hello;
    }

    public void setHello(final String hello) {
        this.hello = hello;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }
}
