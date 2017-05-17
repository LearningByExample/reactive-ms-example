package org.learning.by.example.reactive.microservices.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public final class Location {
    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    final double lat;
    final double lng;

    @JsonCreator
    public Location(@JsonProperty("lat") double lat, @JsonProperty("lng") double lng){
        this.lat = lat;
        this.lng = lng;
    }
}