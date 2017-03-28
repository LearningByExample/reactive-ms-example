package com.juan.medina.microservices.model;

/**
 * Created by mail on 27/03/2017.
 */
public class HelloResponse {

    private String result;

    public HelloResponse() {
        this.result = "KO";
    }

    public HelloResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
