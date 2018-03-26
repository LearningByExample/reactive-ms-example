package org.learning.by.example.reactive.microservices.handlers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.exceptions.GeoLocationNotFoundException;
import org.learning.by.example.reactive.microservices.exceptions.GetGeoLocationException;
import org.learning.by.example.reactive.microservices.exceptions.GetSunriseSunsetException;
import org.learning.by.example.reactive.microservices.model.*;
import org.learning.by.example.reactive.microservices.services.GeoLocationService;
import org.learning.by.example.reactive.microservices.services.SunriseSunsetService;
import org.learning.by.example.reactive.microservices.test.HandlersHelper;
import org.learning.by.example.reactive.microservices.test.tags.UnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@DisplayName("ApiHandler Unit Tests")
class ApiHandlerTests {

    private static final String ADDRESS_VARIABLE = "address";
    private static final String GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA";
    private static final String SUNRISE_TIME = "12:55:17 PM";
    private static final String SUNSET_TIME = "3:14:28 AM";
    private static final double GOOGLE_LAT = 37.4224082;
    private static final double GOOGLE_LNG = -122.0856086;
    private static final String NOT_FOUND = "not found";
    private static final String CANT_GET_LOCATION = "cant get location";
    private static final String CANT_GET_SUNRISE_SUNSET = "can't get sunrise sunset";

    private static final Mono<GeographicCoordinates> GOOGLE_LOCATION = Mono.just(new GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG));
    private static final Mono<SunriseSunset> SUNRISE_SUNSET = Mono.just(new SunriseSunset(SUNRISE_TIME, SUNSET_TIME));
    private static final Mono<GeographicCoordinates> LOCATION_NOT_FOUND = Mono.error(new GeoLocationNotFoundException(NOT_FOUND));
    private static final Mono<GeographicCoordinates> LOCATION_EXCEPTION = Mono.error(new GetGeoLocationException(CANT_GET_LOCATION));
    private static final Mono<GeographicCoordinates> SUNRISE_SUNSET_ERROR = Mono.error(new GetSunriseSunsetException(CANT_GET_SUNRISE_SUNSET));


    @Autowired
    private ApiHandler apiHandler;

    @SpyBean
    private GeoLocationService geoLocationService;

    @SpyBean
    private SunriseSunsetService sunriseSunsetService;

    private Mono<SunriseSunset> getData(final GeographicCoordinates ignore) {
        return SUNRISE_SUNSET;
    }

    @Test
    void combineTest() {
        GOOGLE_LOCATION.zipWhen(this::getData, LocationResponse::new)
                .subscribe(this::verifyLocationResponse);
    }

    private void verifyLocationResponse(final LocationResponse locationResponse) {

        assertThat(locationResponse.getGeographicCoordinates().getLatitude(), is(GOOGLE_LAT));
        assertThat(locationResponse.getGeographicCoordinates().getLongitude(), is(GOOGLE_LNG));

        assertThat(locationResponse.getSunriseSunset().getSunrise(), is(SUNRISE_TIME));
        assertThat(locationResponse.getSunriseSunset().getSunset(), is(SUNSET_TIME));
    }

    @Test
    void serverResponseTest() {
        GOOGLE_LOCATION.zipWhen(this::getData, LocationResponse::new)
                .transform(apiHandler::serverResponse).subscribe(this::verifyServerResponse);
    }

    private void verifyServerResponse(final ServerResponse serverResponse) {

        assertThat(serverResponse.statusCode(), is(HttpStatus.OK));

        final LocationResponse locationResponse = HandlersHelper.extractEntity(serverResponse, LocationResponse.class);

        verifyLocationResponse(locationResponse);
    }

    @Test
    void buildResponseTest() {
        final ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.pathVariable(ADDRESS_VARIABLE)).thenReturn(GOOGLE_ADDRESS);

        doReturn(GOOGLE_LOCATION).when(geoLocationService).fromAddress(any());
        doReturn(SUNRISE_SUNSET).when(sunriseSunsetService).fromGeographicCoordinates(any());

        Mono.just(GOOGLE_ADDRESS).transform(apiHandler::buildResponse).subscribe(this::verifyServerResponse);

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }

    @Test
    void getLocationTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.pathVariable(ADDRESS_VARIABLE)).thenReturn(GOOGLE_ADDRESS);

        doReturn(GOOGLE_LOCATION).when(geoLocationService).fromAddress(any());
        doReturn(SUNRISE_SUNSET).when(sunriseSunsetService).fromGeographicCoordinates(any());

        apiHandler.getLocation(serverRequest).subscribe(this::verifyServerResponse);

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }

    @Test
    void postLocationTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(LocationRequest.class)).thenReturn(Mono.just(new LocationRequest(GOOGLE_ADDRESS)));

        doReturn(GOOGLE_LOCATION).when(geoLocationService).fromAddress(any());
        doReturn(SUNRISE_SUNSET).when(sunriseSunsetService).fromGeographicCoordinates(any());

        apiHandler.postLocation(serverRequest).subscribe(this::verifyServerResponse);

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }

    @Test
    void getLocationNotFoundTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.pathVariable(ADDRESS_VARIABLE)).thenReturn(GOOGLE_ADDRESS);

        doReturn(LOCATION_NOT_FOUND).when(geoLocationService).fromAddress(any());
        doReturn(SUNRISE_SUNSET).when(sunriseSunsetService).fromGeographicCoordinates(any());

        ServerResponse serverResponse = apiHandler.getLocation(serverRequest).block();

        assertThat(serverResponse.statusCode(), is(HttpStatus.NOT_FOUND));

        ErrorResponse error = HandlersHelper.extractEntity(serverResponse, ErrorResponse.class);

        assertThat(error.getError(), is(NOT_FOUND));

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }

    @Test
    void getLocationErrorSunriseSunsetTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.pathVariable(ADDRESS_VARIABLE)).thenReturn(GOOGLE_ADDRESS);

        doReturn(GOOGLE_LOCATION).when(geoLocationService).fromAddress(any());
        doReturn(SUNRISE_SUNSET_ERROR).when(sunriseSunsetService).fromGeographicCoordinates(any());

        ServerResponse serverResponse = apiHandler.getLocation(serverRequest).block();

        assertThat(serverResponse.statusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));

        ErrorResponse error = HandlersHelper.extractEntity(serverResponse, ErrorResponse.class);

        assertThat(error.getError(), is(CANT_GET_SUNRISE_SUNSET));

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }

    @Test
    void getLocationBothServiceErrorTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.pathVariable(ADDRESS_VARIABLE)).thenReturn(GOOGLE_ADDRESS);

        doReturn(LOCATION_EXCEPTION).when(geoLocationService).fromAddress(any());
        doReturn(SUNRISE_SUNSET_ERROR).when(sunriseSunsetService).fromGeographicCoordinates(any());

        ServerResponse serverResponse = apiHandler.getLocation(serverRequest).block();

        assertThat(serverResponse.statusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));

        ErrorResponse error = HandlersHelper.extractEntity(serverResponse, ErrorResponse.class);

        assertThat(error.getError(), is(CANT_GET_LOCATION));

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }
}
