package org.learning.by.example.reactive.microservices.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationResponse {
    private final Location geographicCoordinates;
    private final SunriseSunset sunriseSunset;

    @JsonCreator
    public LocationResponse(@JsonProperty final Location geographicCoordinates,
                            @JsonProperty final SunriseSunset sunriseSunset){
        this.geographicCoordinates = geographicCoordinates;
        this.sunriseSunset = sunriseSunset;
    }

    public Location getGeographicCoordinates() {
        return geographicCoordinates;
    }

    public SunriseSunset getSunriseSunset() {
        return sunriseSunset;
    }
}
