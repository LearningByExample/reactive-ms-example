package org.learning.by.example.reactive.microservices.routers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.handlers.ApiHandler;
import org.learning.by.example.reactive.microservices.handlers.ErrorHandler;
import org.learning.by.example.reactive.microservices.model.*;
import org.learning.by.example.reactive.microservices.services.HelloService;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.learning.by.example.reactive.microservices.test.BasicTest;
import org.learning.by.example.reactive.microservices.test.categories.IntegrationTest;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

@IntegrationTest
@DisplayName("ApiRouter Integration Tests")
class ApiRouterTests extends BasicTest {

    private static final String DEFAULT_VALUE = "world";
    private static final String CUSTOM_VALUE = "reactive";
    private static final String JSON_VALUE = "json";
    private static final String HELLO_PATH = "/api/hello";
    private static final String NAME_ARG = "{name}";
    private static final String WRONG_PATH = "/api/wrong";
    private static final String SUPER_ERROR = "SUPER ERROR";
    private static final String MOCK_QUOTE_CONTENT = "content";

    @Autowired
    private ApiHandler apiHandler;

    @Autowired
    private ErrorHandler errorHandler;

    @SpyBean
    private HelloService helloService;

    @SpyBean
    private QuoteService quoteService;

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

}
