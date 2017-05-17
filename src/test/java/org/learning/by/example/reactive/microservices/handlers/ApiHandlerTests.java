package org.learning.by.example.reactive.microservices.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.exceptions.LocationNotFoundException;
import org.learning.by.example.reactive.microservices.model.*;
import org.learning.by.example.reactive.microservices.services.LocationService;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.learning.by.example.reactive.microservices.test.HandlersHelper;
import org.learning.by.example.reactive.microservices.test.categories.UnitTest;
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
    private static final double GOOGLE_LAT = 37.4224082;
    private static final double GOOGLE_LNG = -122.0856086;
    private static final String NOT_FOUND = "not found";

    @Autowired
    private ApiHandler apiHandler;

    @SpyBean
    private QuoteService quoteService;

    @SpyBean
    private LocationService locationService;

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

    private static final Mono<Location> GOOGLE_LOCATION = Mono.just(new Location(GOOGLE_LAT, GOOGLE_LNG));
    private static final Mono<Location> LOCATION_NOT_FOUND = Mono.error(new LocationNotFoundException(NOT_FOUND));

    @Test
    void getLocationTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.pathVariable(ADDRESS_VARIABLE)).thenReturn(GOOGLE_ADDRESS);

        doReturn(GOOGLE_LOCATION).when(locationService).fromAddress(any());

        ServerResponse serverResponse = apiHandler.getLocation(serverRequest).block();

        Location location = HandlersHelper.extractEntity(serverResponse, Location.class);
        assertThat(location.getLatitude(), is(GOOGLE_LAT));
        assertThat(location.getLongitude(), is(GOOGLE_LNG));

        reset(locationService);
    }

    @Test
    void getLocationNotFoundTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.pathVariable(ADDRESS_VARIABLE)).thenReturn(GOOGLE_ADDRESS);

        doReturn(LOCATION_NOT_FOUND).when(locationService).fromAddress(any());

        ServerResponse serverResponse = apiHandler.getLocation(serverRequest).block();

        ErrorResponse error = HandlersHelper.extractEntity(serverResponse, ErrorResponse.class);
        assertThat(error.getError(), is(NOT_FOUND));

        reset(locationService);
    }

}
