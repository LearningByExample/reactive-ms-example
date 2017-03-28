package com.juan.medina.microservices.model;

/**
 * Created by mail on 28/03/2017.
 */
public class HelloRequest {
    private String name;

    public HelloRequest() {
        this.name = "";
    }

    public HelloRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
