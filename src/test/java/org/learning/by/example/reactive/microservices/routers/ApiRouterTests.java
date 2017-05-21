package org.learning.by.example.reactive.microservices.routers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.exceptions.GeoLocationNotFoundException;
import org.learning.by.example.reactive.microservices.handlers.ApiHandler;
import org.learning.by.example.reactive.microservices.handlers.ErrorHandler;
import org.learning.by.example.reactive.microservices.model.*;
import org.learning.by.example.reactive.microservices.services.GeoLocationService;
import org.learning.by.example.reactive.microservices.services.HelloService;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.learning.by.example.reactive.microservices.services.SunriseSunsetService;
import org.learning.by.example.reactive.microservices.test.BasicIntegrationTest;
import org.learning.by.example.reactive.microservices.test.tags.IntegrationTest;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

@IntegrationTest
@DisplayName("ApiRouter Integration Tests")
class ApiRouterTests extends BasicIntegrationTest {

    private static final String DEFAULT_VALUE = "world";
    private static final String CUSTOM_VALUE = "reactive";
    private static final String JSON_VALUE = "json";
    private static final String HELLO_PATH = "/api/hello";
    private static final String NAME_ARG = "{name}";
    private static final String LOCATION_PATH = "/api/location";
    private static final String ADDRESS_ARG = "{address}";
    private static final String WRONG_PATH = "/api/wrong";
    private static final String SUPER_ERROR = "SUPER ERROR";
    private static final String MOCK_QUOTE_CONTENT = "content";
    private static final String GOOGLE_ADDRESS = "1600 Amphitheatre Parkway, Mountain View, CA";
    private static final double GOOGLE_LAT = 37.4224082;
    private static final double GOOGLE_LNG = -122.0856086;
    private static final String NOT_FOUND = "not found";
    private static final String BIG_ERROR = "big error";
    private static final String SUNRISE_TIME = "12:55:17 PM";
    private static final String SUNSET_TIME = "3:14:28 AM";

    private static final Mono<GeographicCoordinates> GOOGLE_LOCATION = Mono.just(new GeographicCoordinates(GOOGLE_LAT, GOOGLE_LNG));
    private static final Mono<GeographicCoordinates> LOCATION_NOT_FOUND = Mono.error(new GeoLocationNotFoundException(NOT_FOUND));
    private static final Mono<GeographicCoordinates> GENERIC_ERROR = Mono.error(new RuntimeException(BIG_ERROR));
    private static final Mono<SunriseSunset> SUNRISE_SUNSET = Mono.just(new SunriseSunset(SUNRISE_TIME, SUNSET_TIME));

    @Autowired
    private ApiHandler apiHandler;

    @Autowired
    private ErrorHandler errorHandler;

    @SpyBean
    private HelloService helloService;

    @SpyBean
    private QuoteService quoteService;

    @SpyBean
    private GeoLocationService geoLocationService;

    @SpyBean
    private SunriseSunsetService sunriseSunsetService;

    @BeforeEach
    void setup() {
        super.bindToRouterFunction(ApiRouter.doRoute(apiHandler, errorHandler));

        doReturn(createMockedQuote(MOCK_QUOTE_CONTENT)).when(quoteService).get();

        final ApiRouter apiRouter = new ApiRouter();
    }

    private Mono<Quote> createMockedQuote(final String content) {
        Quote quote = new Quote();

        quote.setContent(content);

        return Mono.just(quote);
    }

    @AfterEach
    void tearDown() {
        reset(quoteService);
    }

    @Test
    void defaultHelloTest() {
        final HelloResponse response = get(
                builder -> builder.path(HELLO_PATH).build(),
                HelloResponse.class);

        assertThat(response.getGreetings(), is(DEFAULT_VALUE));
        assertThat(response.getQuote(), is(MOCK_QUOTE_CONTENT));
    }

    @Test
    void getHelloTest() {
        final HelloResponse response = get(
                builder -> builder.path(HELLO_PATH).path("/").path(NAME_ARG).build(CUSTOM_VALUE),
                HelloResponse.class);

        assertThat(response.getGreetings(), is(CUSTOM_VALUE));
        assertThat(response.getQuote(), is(MOCK_QUOTE_CONTENT));
    }

    @Test
    void postHelloTest() {
        final HelloResponse response = post(
                builder -> builder.path(HELLO_PATH).build(),
                new HelloRequest(JSON_VALUE),
                HelloResponse.class);

        assertThat(response.getGreetings(), is(JSON_VALUE));
        assertThat(response.getQuote(), is(MOCK_QUOTE_CONTENT));
    }

    @Test
    void getWrongPath() {
        final ErrorResponse response = get(
                builder -> builder.path(WRONG_PATH).build(),
                HttpStatus.NOT_FOUND,
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

    @Test
    void postWrongPath() {
        final ErrorResponse response = post(
                builder -> builder.path(WRONG_PATH).build(),
                HttpStatus.NOT_FOUND,
                new HelloRequest(JSON_VALUE),
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

    @Test
    void postWrongObject() {
        final ErrorResponse response = post(
                builder -> builder.path(HELLO_PATH).build(),
                HttpStatus.BAD_REQUEST,
                new WrongRequest(JSON_VALUE),
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

    @Test
    void postLocationWrongObject() {
        final ErrorResponse response = post(
                builder -> builder.path(LOCATION_PATH).build(),
                HttpStatus.BAD_REQUEST,
                new WrongRequest(JSON_VALUE),
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

    @Test
    void helloServiceErrorTest() {

        doReturn(Mono.error(new RuntimeException(SUPER_ERROR))).when(helloService).greetings(Mockito.any());

        final ErrorResponse response = get(
                builder -> builder.path(HELLO_PATH).build(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorResponse.class);

        assertThat(response.getError(), is(SUPER_ERROR));

        reset(helloService);
    }

    @Test
    void quoteServiceErrorTest() {

        doReturn(Mono.error(new RuntimeException(SUPER_ERROR))).when(quoteService).get();

        final ErrorResponse response = get(
                builder -> builder.path(HELLO_PATH).build(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorResponse.class);

        assertThat(response.getError(), is(SUPER_ERROR));

        reset(helloService);
    }

    @Test
    void getLocationTest(){

        doReturn(GOOGLE_LOCATION).when(geoLocationService).fromAddress(any());
        doReturn(SUNRISE_SUNSET).when(sunriseSunsetService).fromGeographicCoordinates(any());

        final LocationResponse location = get(
                builder -> builder.path(LOCATION_PATH).path("/").path(ADDRESS_ARG).build(GOOGLE_ADDRESS),
                LocationResponse.class);

        assertThat(location.getGeographicCoordinates().getLatitude(), is(GOOGLE_LAT));
        assertThat(location.getGeographicCoordinates().getLongitude(), is(GOOGLE_LNG));

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }

    @Test
    void postLocationTest(){

        doReturn(GOOGLE_LOCATION).when(geoLocationService).fromAddress(any());
        doReturn(SUNRISE_SUNSET).when(sunriseSunsetService).fromGeographicCoordinates(any());

        final LocationResponse location = post(
                builder -> builder.path(LOCATION_PATH).build(),
                new LocationRequest(GOOGLE_ADDRESS),
                LocationResponse.class);

        assertThat(location.getGeographicCoordinates().getLatitude(), is(GOOGLE_LAT));
        assertThat(location.getGeographicCoordinates().getLongitude(), is(GOOGLE_LNG));

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }

    @Test
    void getLocationNotFoundTest(){

        doReturn(LOCATION_NOT_FOUND).when(geoLocationService).fromAddress(any());
        doReturn(SUNRISE_SUNSET).when(sunriseSunsetService).fromGeographicCoordinates(any());

        final ErrorResponse errorResponse = get(
                builder -> builder.path(LOCATION_PATH).path("/").path(ADDRESS_ARG).build(GOOGLE_ADDRESS),
                HttpStatus.NOT_FOUND,
                ErrorResponse.class);

        assertThat(errorResponse.getError(), is(NOT_FOUND));

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }

    @Test
    void getLocationExceptionTest(){

        doReturn(GENERIC_ERROR).when(geoLocationService).fromAddress(any());
        doReturn(SUNRISE_SUNSET).when(sunriseSunsetService).fromGeographicCoordinates(any());

        final ErrorResponse errorResponse = get(
                builder -> builder.path(LOCATION_PATH).path("/").path(ADDRESS_ARG).build(GOOGLE_ADDRESS),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorResponse.class);

        assertThat(errorResponse.getError(), is(BIG_ERROR));

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }

    @Test
    void getLocationSunriseSunsetExceptionTest(){

        doReturn(GOOGLE_LOCATION).when(geoLocationService).fromAddress(any());
        doReturn(GENERIC_ERROR).when(sunriseSunsetService).fromGeographicCoordinates(any());

        final ErrorResponse errorResponse = get(
                builder -> builder.path(LOCATION_PATH).path("/").path(ADDRESS_ARG).build(GOOGLE_ADDRESS),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorResponse.class);

        assertThat(errorResponse.getError(), is(BIG_ERROR));

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }

    @Test
    void getLocationBothServiceExceptionTest(){

        doReturn(GENERIC_ERROR).when(geoLocationService).fromAddress(any());
        doReturn(GENERIC_ERROR).when(sunriseSunsetService).fromGeographicCoordinates(any());

        final ErrorResponse errorResponse = get(
                builder -> builder.path(LOCATION_PATH).path("/").path(ADDRESS_ARG).build(GOOGLE_ADDRESS),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorResponse.class);

        assertThat(errorResponse.getError(), is(BIG_ERROR));

        reset(geoLocationService);
        reset(sunriseSunsetService);
    }

}
