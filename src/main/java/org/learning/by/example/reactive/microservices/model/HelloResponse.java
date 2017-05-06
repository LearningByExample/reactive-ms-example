package org.learning.by.example.reactive.microservices.model;

public class HelloResponse {

    private String greetings;
    private String quote;

    public HelloResponse() {
        this.greetings = "";
        this.quote = "";
    }

    public HelloResponse(final String greetings, final String quote) {
        this.greetings = greetings;
        this.quote = quote;
    }

    public String getGreetings() {
        return greetings;
    }

    public void setGreetings(final String greetings) {
        this.greetings = greetings;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }
}
