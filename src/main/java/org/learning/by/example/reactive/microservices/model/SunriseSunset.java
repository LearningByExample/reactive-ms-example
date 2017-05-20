package org.learning.by.example.reactive.microservices.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SunriseSunset {
    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    private final String sunrise;
    private final String sunset;

    @JsonCreator
    public SunriseSunset(@JsonProperty("sunrise") final String sunrise, @JsonProperty("sunset") final String sunset) {
        this.sunrise = sunrise;
        this.sunset = sunset;
    }
}
