package org.learning.by.example.reactive.microservices.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationRequest {

    private final String address;

    @JsonCreator
    public LocationRequest(@JsonProperty("address") final String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
