package org.learning.by.example.reactive.microservices.routers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.model.GeographicCoordinates;
import org.learning.by.example.reactive.microservices.model.SunriseSunset;
import org.learning.by.example.reactive.microservices.services.GeoLocationService;
import org.learning.by.example.reactive.microservices.services.SunriseSunsetService;
import org.learning.by.example.reactive.microservices.test.BasicIntegrationTest;
import org.learning.by.example.reactive.microservices.test.tags.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

@IntegrationTest
@DisplayName("MainRouter Integration Tests")
class MainRouterTest extends BasicIntegrationTest {

    private static final String STATIC_ROUTE = "/index.html";
    private static final String LOCATION_PATH = "/api/location";
    private static final String ADDRESS_ARG = "{address}";
    private static final double GOOGLE_LAT = 37.4224082;
    private static final double GOOGLE_LNG = -122.0856086;
    private static final String GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA";
    private static final String SUNRISE_TIME = "12:55:17 PM";
    private static final String SUNSET_TIME = "3:14:28 AM";

    private static final Mono<GeographicCoordinates> GOOGLE_LOCATION = Mono.just(new GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG));
    private static final Mono<SunriseSunset> SUNRISE_SUNSET = Mono.just(new SunriseSunset(SUNRISE_TIME, SUNSET_TIME));

    @SpyBean
    private GeoLocationService geoLocationService;

    @SpyBean
    private SunriseSunsetService sunriseSunsetService;

    @Autowired
    private RouterFunction<?> mainRouterFunction;


    @BeforeEach
    void setup() {
        super.bindToRouterFunction(mainRouterFunction);
    }

    @BeforeAll
    static void setupAll() {
        final MainRouter mainRouter = new MainRouter();
    }

    @Test
    void staticRouteTest() {
        get(builder -> builder.path(STATIC_ROUTE).build());
    }

    @Test
    void apiRouteTest() {

        doReturn(GOOGLE_LOCATION).when(geoLocationService).fromAddress(any());
        doReturn(SUNRISE_SUNSET).when(sunriseSunsetService).fromGeographicCoordinates(any());

        get(
                builder -> builder.path(LOCATION_PATH).path("/").path(ADDRESS_ARG).build(GOOGLE_ADDRESS),
                String.class
        );

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }
}
