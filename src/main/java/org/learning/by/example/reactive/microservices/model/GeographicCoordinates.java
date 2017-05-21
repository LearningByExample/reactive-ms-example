package org.learning.by.example.reactive.microservices.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public final class GeographicCoordinates {

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private final double latitude;
    private final double longitude;

    @JsonCreator
    public GeographicCoordinates(@JsonProperty("latitude") double latitude, @JsonProperty("longitude") double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
