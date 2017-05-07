package org.learning.by.example.reactive.microservices.handlers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.learning.by.example.reactive.microservices.model.Quote;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.learning.by.example.reactive.microservices.test.HandlersHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ApiHandlerTests {
    private static final String MOCK_QUOTE_CONTENT = "content";
    private static final String SAMPLE_NAME = "name";

    @Autowired
    private ApiHandler apiHandler;

    @SpyBean
    private QuoteService quoteService;

    @Before
    public void setup() {
        given(quoteService.getQuote()).willReturn(() ->
                createMockedQuote(MOCK_QUOTE_CONTENT)
        );
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
    public void getQuoteTest() {
        apiHandler.getQuote().subscribe(content -> {
            assertThat(content, is(MOCK_QUOTE_CONTENT));
        });
    }

    @Test
    public void createHelloResponseTest() {
        Mono.just(SAMPLE_NAME).publish(apiHandler.createHelloResponse())
                .subscribe(helloResponse -> {
                    assertThat(helloResponse.getQuote(), is(MOCK_QUOTE_CONTENT));
                    assertThat(helloResponse.getGreetings(), is(SAMPLE_NAME));
                });

    }

    @Test
    public void convertToServerResponseTest() {
        Mono.just(SAMPLE_NAME).publish(apiHandler.createHelloResponse())
                .publish(apiHandler.convertToServerResponse())
                .subscribe(serverResponse -> {
                    assertThat(serverResponse.statusCode(), is(HttpStatus.OK));

                    HelloResponse helloResponse = HandlersHelper.extractEntity(serverResponse, HelloResponse.class);
                    assertThat(helloResponse.getQuote(), is(MOCK_QUOTE_CONTENT));
                    assertThat(helloResponse.getGreetings(), is(SAMPLE_NAME));
                });
    }
}
