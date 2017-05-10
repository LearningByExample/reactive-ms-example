package org.learning.by.example.reactive.microservices.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@UnitTest
@DisplayName("ApiHandler Unit Tests")
@SpringBootTest(classes = ReactiveMsApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ApiHandlerTests {
    private static final String MOCK_QUOTE_CONTENT = "content";
    private static final String DEFAULT_NAME = "world";
    private static final String NAME_VARIABLE = "name";

    @Autowired
    private ApiHandler apiHandler;

    @SpyBean
    private QuoteService quoteService;

    @BeforeEach
    void setup() {
        doReturn(createMockedQuote(MOCK_QUOTE_CONTENT)).when(quoteService).get();
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
    void randomQuoteTest() {
        apiHandler.randomQuote().subscribe(content -> {
            assertThat(content, is(MOCK_QUOTE_CONTENT));
        });
    }

    @Test
    void createHelloResponseTest() {
        Mono.just(DEFAULT_NAME).publish(apiHandler::createHelloResponse)
                .subscribe(helloResponse -> {
                    assertThat(helloResponse.getQuote(), is(MOCK_QUOTE_CONTENT));
                    assertThat(helloResponse.getGreetings(), is(DEFAULT_NAME));
                });

    }

    @Test
    void convertToServerResponseTest() {
        Mono.just(DEFAULT_NAME).publish(apiHandler::createHelloResponse)
                .publish(apiHandler::convertToServerResponse)
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
        Mono.just(DEFAULT_NAME).publish(apiHandler::getServerResponse)
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
}
