package org.learning.by.example.reactive.microservices.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.exceptions.GetGeoLocationException;
import org.learning.by.example.reactive.microservices.exceptions.GetSunriseSunsetException;
import org.learning.by.example.reactive.microservices.exceptions.LocationNotFoundException;
import org.learning.by.example.reactive.microservices.model.*;
import org.learning.by.example.reactive.microservices.services.GeoLocationService;
import org.learning.by.example.reactive.microservices.services.QuoteService;
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
    private static final String MOCK_QUOTE_CONTENT = "content";
    private static final String DEFAULT_NAME = "world";
    private static final String NAME_VARIABLE = "name";
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
    private static final Mono<GeographicCoordinates> LOCATION_NOT_FOUND = Mono.error(new LocationNotFoundException(NOT_FOUND));
    private static final Mono<GeographicCoordinates> LOCATION_EXCEPTION = Mono.error(new GetGeoLocationException(CANT_GET_LOCATION));
    private static final Mono<GeographicCoordinates> SUNRISE_SUNSET_ERROR = Mono.error(new GetSunriseSunsetException(CANT_GET_SUNRISE_SUNSET));


    @Autowired
    private ApiHandler apiHandler;

    @SpyBean
    private QuoteService quoteService;

    @SpyBean
    private GeoLocationService geoLocationService;

    @SpyBean
    private SunriseSunsetService sunriseSunsetService;

    @BeforeEach
    void setup() {
        doReturn(createMockedQuoteMono(MOCK_QUOTE_CONTENT)).when(quoteService).get();
    }

    private Mono<Quote> createMockedQuoteMono(final String content) {
        return Mono.just(createQuote(content));
    }

    private Quote createQuote(final String content) {
        Quote quote = new Quote();
        quote.setContent(content);
        return quote;
    }

    @AfterEach
    void tearDown() {
        reset(quoteService);
    }

    @Test
    void combineGreetingAndQuoteTest() {

        HelloResponse helloResponse = apiHandler.combineGreetingAndQuote(DEFAULT_NAME, createQuote(MOCK_QUOTE_CONTENT));

        assertThat(helloResponse.getGreetings(), is(DEFAULT_NAME));
        assertThat(helloResponse.getQuote(), is(MOCK_QUOTE_CONTENT));
    }

    @Test
    void createHelloResponseTest() {
        Mono.just(DEFAULT_NAME).transform(apiHandler::createHelloResponse)
                .subscribe(helloResponse -> {
                    assertThat(helloResponse.getQuote(), is(MOCK_QUOTE_CONTENT));
                    assertThat(helloResponse.getGreetings(), is(DEFAULT_NAME));
                });

    }

    @Test
    void convertToServerResponseTest() {
        Mono.just(DEFAULT_NAME).transform(apiHandler::createHelloResponse)
                .transform(apiHandler::convertToServerResponse)
                .subscribe(this::checkResponse);
    }

    private void checkResponse(final ServerResponse serverResponse) {
        assertThat(serverResponse.statusCode(), is(HttpStatus.OK));

        HelloResponse helloResponse = HandlersHelper.extractEntity(serverResponse, HelloResponse.class);
        assertThat(helloResponse.getQuote(), is(MOCK_QUOTE_CONTENT));
        assertThat(helloResponse.getGreetings(), is(DEFAULT_NAME));
    }

    @Test
    void getServerResponseTest() {
        Mono.just(DEFAULT_NAME).transform(apiHandler::getServerResponse)
                .subscribe(this::checkResponse);
    }

    @Test
    void defaultHelloTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        apiHandler.defaultHello(serverRequest).subscribe(this::checkResponse);
    }

    @Test
    void getHelloTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.pathVariable(NAME_VARIABLE)).thenReturn(DEFAULT_NAME);

        apiHandler.getHello(serverRequest).subscribe(this::checkResponse);
    }

    @Test
    void postHelloTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(HelloRequest.class)).thenReturn(Mono.just(new HelloRequest(DEFAULT_NAME)));

        apiHandler.postHello(serverRequest).subscribe(this::checkResponse);
    }

    @Test
    void combineTest(){
        GOOGLE_LOCATION.and(SUNRISE_SUNSET, LocationResponse::new)
                .subscribe(this::verifyLocationResponse);
    }

    void verifyLocationResponse(LocationResponse locationResponse){

        assertThat(locationResponse.getGeographicCoordinates().getLatitude(), is(GOOGLE_LAT));
        assertThat(locationResponse.getGeographicCoordinates().getLongitude(), is(GOOGLE_LNG));

        assertThat(locationResponse.getSunriseSunset().getSunrise(), is(SUNRISE_TIME));
        assertThat(locationResponse.getSunriseSunset().getSunset(), is(SUNSET_TIME));
    }

    @Test
    void responseTest(){
        GOOGLE_LOCATION.and(SUNRISE_SUNSET, LocationResponse::new)
                .transform(apiHandler::response).subscribe(this::verifyServerResponse);
    }

    void verifyServerResponse(ServerResponse serverResponse){

        assertThat(serverResponse.statusCode(), is(HttpStatus.OK));

        LocationResponse location = HandlersHelper.extractEntity(serverResponse, LocationResponse.class);

        verifyLocationResponse(location);
    }

    @Test
    void fromGeographicCoordinatesTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.pathVariable(ADDRESS_VARIABLE)).thenReturn(GOOGLE_ADDRESS);

        doReturn(GOOGLE_LOCATION).when(geoLocationService).fromAddress(any());
        doReturn(SUNRISE_SUNSET).when(sunriseSunsetService).fromGeographicCoordinates(any());

        apiHandler.getLocation(serverRequest).subscribe(this::verifyServerResponse);

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }

    @Test
    void fromGeographicCoordinatesNotFoundTest() {
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
    void fromGeographicCoordinatesErrorSunriseSunsetTest() {
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
    void fromGeographicCoordinatesBothServiceErrorTest() {
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
