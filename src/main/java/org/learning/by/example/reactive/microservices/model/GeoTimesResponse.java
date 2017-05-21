package org.learning.by.example.reactive.microservices.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class GeoTimesResponse {

    private final Results results;
    private final String status;

    @JsonCreator
    public GeoTimesResponse(@JsonProperty("results") Results results, @JsonProperty("status") String status) {
        this.results = results;
        this.status = status;
    }

    public Results getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public static final class Results {
        public String getSunrise() {
            return sunrise;
        }

        public String getSunset() {
            return sunset;
        }

        final String sunrise;
        final String sunset;
        final String solar_noon;
        final long day_length;
        final String civil_twilight_begin;
        final String civil_twilight_end;
        final String nautical_twilight_begin;
        final String nautical_twilight_end;
        final String astronomical_twilight_begin;
        final String astronomical_twilight_end;

        @JsonCreator
        public Results(@JsonProperty("sunrise") String sunrise, @JsonProperty("sunset") String sunset, @JsonProperty("solar_noon") String solar_noon, @JsonProperty("day_length") long day_length, @JsonProperty("civil_twilight_begin") String civil_twilight_begin, @JsonProperty("civil_twilight_end") String civil_twilight_end, @JsonProperty("nautical_twilight_begin") String nautical_twilight_begin, @JsonProperty("nautical_twilight_end") String nautical_twilight_end, @JsonProperty("astronomical_twilight_begin") String astronomical_twilight_begin, @JsonProperty("astronomical_twilight_end") String astronomical_twilight_end) {
            this.sunrise = sunrise;
            this.sunset = sunset;
            this.solar_noon = solar_noon;
            this.day_length = day_length;
            this.civil_twilight_begin = civil_twilight_begin;
            this.civil_twilight_end = civil_twilight_end;
            this.nautical_twilight_begin = nautical_twilight_begin;
            this.nautical_twilight_end = nautical_twilight_end;
            this.astronomical_twilight_begin = astronomical_twilight_begin;
            this.astronomical_twilight_end = astronomical_twilight_end;
        }
    }
}
