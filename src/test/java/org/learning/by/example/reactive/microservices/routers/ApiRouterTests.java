package org.learning.by.example.reactive.microservices.routers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.handlers.ApiHandler;
import org.learning.by.example.reactive.microservices.handlers.ErrorHandler;
import org.learning.by.example.reactive.microservices.model.*;
import org.learning.by.example.reactive.microservices.services.HelloService;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.learning.by.example.reactive.microservices.test.BasicRouterTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiRouterTests extends BasicRouterTest {

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

    @Before
    public void setup() {
        super.setup(ApiRouter.doRoute(apiHandler, errorHandler));
        given(quoteService.getQuote()).willReturn( ()->
                createMockedQuote(MOCK_QUOTE_CONTENT)
        );
    }

    @After
    public void tearDown(){
        reset(quoteService);
    }

    private Mono<Quote> createMockedQuote(final String content) {
        Quote quote = new Quote();

        quote.setContent(content);

        return Mono.just(quote);
    }

    @Test
    public void defaultHelloTest() {
        final HelloResponse response = get(
                builder -> builder.path(HELLO_PATH).build(),
                HelloResponse.class);

        assertThat(response.getGreetings(), is(DEFAULT_VALUE));
        assertThat(response.getQuote(), is(MOCK_QUOTE_CONTENT));
    }

    @Test
    public void getHelloTest() {
        final HelloResponse response = get(
                builder -> builder.path(HELLO_PATH).path("/").path(NAME_ARG).build(CUSTOM_VALUE),
                HelloResponse.class);

        assertThat(response.getGreetings(), is(CUSTOM_VALUE));
        assertThat(response.getQuote(), is(MOCK_QUOTE_CONTENT));

    }

    @Test
    public void postHelloTest() {
        final HelloResponse response = post(
                builder -> builder.path(HELLO_PATH).build(),
                new HelloRequest(JSON_VALUE),
                HelloResponse.class);

        assertThat(response.getGreetings(), is(JSON_VALUE));
        assertThat(response.getQuote(), is(MOCK_QUOTE_CONTENT));

    }

    @Test
    public void getWrongPath() {
        final ErrorResponse response = get(
                builder -> builder.path(WRONG_PATH).build(),
                HttpStatus.NOT_FOUND,
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

    @Test
    public void postWrongPath() {
        final ErrorResponse response = post(
                builder -> builder.path(WRONG_PATH).build(),
                HttpStatus.NOT_FOUND,
                new HelloRequest(JSON_VALUE),
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

    @Test
    public void postWrongObject() {
        final ErrorResponse response = post(
                builder -> builder.path(HELLO_PATH).build(),
                HttpStatus.BAD_REQUEST,
                new WrongRequest(JSON_VALUE),
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

    @Test
    public void helloServiceErrorTest() {

        given(helloService.getGreetings()).willReturn(name -> Mono.error(new RuntimeException(SUPER_ERROR)));

        final ErrorResponse response = get(
                builder -> builder.path(HELLO_PATH).build(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorResponse.class);

        assertThat(response.getError(), is(SUPER_ERROR));

        reset(helloService);
    }

    @Test
    public void quoteServiceErrorTest() {

        given(quoteService.getQuote()).willReturn( () -> Mono.error(new RuntimeException(SUPER_ERROR)));

        final ErrorResponse response = get(
                builder -> builder.path(HELLO_PATH).build(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorResponse.class);

        assertThat(response.getError(), is(SUPER_ERROR));

        reset(helloService);
    }

}
