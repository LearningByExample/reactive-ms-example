package org.learning.by.example.reactive.microservices.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.model.LocationRequest;
import org.learning.by.example.reactive.microservices.model.LocationResponse;
import org.learning.by.example.reactive.microservices.test.BasicIntegrationTest;
import org.learning.by.example.reactive.microservices.test.tags.SystemTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SystemTest
@DisplayName("ReactiveMsApplication System Tests")
class ReactiveMsApplicationTest extends BasicIntegrationTest {

    private static final String LOCATION_PATH = "/api/location";
    private static final String ADDRESS_ARG = "{address}";
    private static final String GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA";


    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        bindToServerPort(port);
    }

    @Test
    @DisplayName("get location from URL")
    void getLocationTest() {
        final LocationResponse response = get(
                builder -> builder.path(LOCATION_PATH).path("/").path(ADDRESS_ARG).build(GOOGLE_ADDRESS),
                LocationResponse.class);

        assertThat(response.getGeographicCoordinates(), not(nullValue()));
        assertThat(response.getGeographicCoordinates().getLatitude(), not(nullValue()));
        assertThat(response.getGeographicCoordinates().getLongitude(), not(nullValue()));

        assertThat(response.getSunriseSunset(), not(nullValue()));
        assertThat(response.getSunriseSunset().getSunrise(), not(isEmptyOrNullString()));
        assertThat(response.getSunriseSunset().getSunset(), not(isEmptyOrNullString()));
    }

    @Test
    @DisplayName("post location")
    void postLocationTest() {
        final LocationResponse response = post(
                builder -> builder.path(LOCATION_PATH).build(),
                new LocationRequest(GOOGLE_ADDRESS),
                LocationResponse.class);

        assertThat(response.getGeographicCoordinates(), not(nullValue()));
        assertThat(response.getGeographicCoordinates().getLatitude(), not(nullValue()));
        assertThat(response.getGeographicCoordinates().getLongitude(), not(nullValue()));

        assertThat(response.getSunriseSunset(), not(nullValue()));
        assertThat(response.getSunriseSunset().getSunrise(), not(isEmptyOrNullString()));
        assertThat(response.getSunriseSunset().getSunset(), not(isEmptyOrNullString()));
    }
}
