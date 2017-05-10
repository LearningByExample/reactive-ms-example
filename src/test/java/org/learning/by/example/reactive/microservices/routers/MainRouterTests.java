package org.learning.by.example.reactive.microservices.routers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.learning.by.example.reactive.microservices.application.ReactiveMsApplication;
import org.learning.by.example.reactive.microservices.model.Quote;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.learning.by.example.reactive.microservices.test.BasicTest;
import org.learning.by.example.reactive.microservices.test.categories.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

@IntegrationTest
@DisplayName("MainRouter Integration Tests")
@SpringBootTest(classes = ReactiveMsApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class MainRouterTests extends BasicTest {

    private static final String STATIC_ROUTE = "/index.html";
    private static final String API_ROUTE = "/api/hello";
    private static final String MOCK_QUOTE_CONTENT = "content";


    @Autowired
    private RouterFunction<?> mainRouterFunction;

    @SpyBean
    private QuoteService quoteService;

    @BeforeEach
    void setup() {
        super.bindToRouterFunction(mainRouterFunction);

        doReturn(createMockedQuote(MOCK_QUOTE_CONTENT)).when(quoteService).get();

        final MainRouter mainRouter = new MainRouter();
    }

    @AfterEach
    void tearDown(){
        reset(quoteService);
    }

    private Mono<Quote> createMockedQuote(final String content) {
        Quote quote = new Quote();

        quote.setContent(content);

        return Mono.just(quote);
    }

    @Test
    void staticRouteTest() {
        get(builder -> builder.path(STATIC_ROUTE).build());
    }

    @Test
    void apiRouteTest() {
        get(
                builder -> builder.path(API_ROUTE).build(),
                String.class
        );
    }
}
