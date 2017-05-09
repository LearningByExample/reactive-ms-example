package org.learning.by.example.reactive.microservices.handlers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.application.ReactiveMsApplication;
import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.learning.by.example.reactive.microservices.model.Quote;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.learning.by.example.reactive.microservices.test.HandlersHelper;
import org.learning.by.example.reactive.microservices.test.categories.UnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReactiveMsApplication.class)
@ActiveProfiles("test")
@Category(UnitTest.class)
public class ApiHandlerTests {
    private static final String MOCK_QUOTE_CONTENT = "content";
    private static final String DEFAULT_NAME = "world";
    private static final String NAME_VARIABLE = "name";

    @Autowired
    private ApiHandler apiHandler;

    @SpyBean
    private QuoteService quoteService;

    @Before
    public void setup() {
        doReturn(createMockedQuote(MOCK_QUOTE_CONTENT)).when(quoteService).get();
    }

    private Mono<Quote> createMockedQuote(final String content) {
        Quote quote = new Quote();

        quote.setContent(content);

        return Mono.just(quote);
    }

    @After
    public void tearDown() {
        reset(quoteService);
    }

    @Test
    public void randomQuoteTest() {
        apiHandler.randomQuote().subscribe(content -> {
            assertThat(content, is(MOCK_QUOTE_CONTENT));
        });
    }

    @Test
    public void createHelloResponseTest() {
        Mono.just(DEFAULT_NAME).publish(apiHandler::createHelloResponse)
                .subscribe(helloResponse -> {
                    assertThat(helloResponse.getQuote(), is(MOCK_QUOTE_CONTENT));
                    assertThat(helloResponse.getGreetings(), is(DEFAULT_NAME));
                });

    }

    @Test
    public void convertToServerResponseTest() {
        Mono.just(DEFAULT_NAME).publish(apiHandler::createHelloResponse)
                .publish(apiHandler::convertToServerResponse)
                .subscribe(this::checkResponse);
    }

    public void checkResponse(final ServerResponse serverResponse) {
        assertThat(serverResponse.statusCode(), is(HttpStatus.OK));

        HelloResponse helloResponse = HandlersHelper.extractEntity(serverResponse, HelloResponse.class);
        assertThat(helloResponse.getQuote(), is(MOCK_QUOTE_CONTENT));
        assertThat(helloResponse.getGreetings(), is(DEFAULT_NAME));
    }

    @Test
    public void getServerResponseTest() {
        Mono.just(DEFAULT_NAME).publish(apiHandler::getServerResponse)
                .subscribe(this::checkResponse);
    }

    @Test
    public void defaultHelloTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        apiHandler.defaultHello(serverRequest).subscribe(this::checkResponse);
    }

    @Test
    public void getHelloTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.pathVariable(NAME_VARIABLE)).thenReturn(DEFAULT_NAME);

        apiHandler.getHello(serverRequest).subscribe(this::checkResponse);
    }

    @Test
    public void postHelloTest() {
        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(HelloRequest.class)).thenReturn(Mono.just(new HelloRequest(DEFAULT_NAME)));

        apiHandler.postHello(serverRequest).subscribe(this::checkResponse);
    }
}
