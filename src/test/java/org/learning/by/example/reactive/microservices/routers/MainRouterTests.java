package org.learning.by.example.reactive.microservices.routers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.model.Quote;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.learning.by.example.reactive.microservices.test.BasicIntegrationTest;
import org.learning.by.example.reactive.microservices.test.categories.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Category(IntegrationTest.class)
public class MainRouterTests extends BasicIntegrationTest {

    private static final String STATIC_ROUTE = "/index.html";
    private static final String API_ROUTE = "/api/hello";
    private static final String MOCK_QUOTE_CONTENT = "content";


    @Autowired
    private RouterFunction<?> mainRouterFunction;

    @SpyBean
    private QuoteService quoteService;

    @Before
    public void setup() {
        super.bindToRouterFunction(mainRouterFunction);

        given(quoteService.getQuote()).willReturn( () ->
                createMockedQuote(MOCK_QUOTE_CONTENT)
        );
        final MainRouter mainRouter = new MainRouter();
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
    public void staticRouteTest() {
        get(builder -> builder.path(STATIC_ROUTE).build());
    }

    @Test
    public void apiRouteTest() {
        get(
                builder -> builder.path(API_ROUTE).build(),
                String.class
        );
    }
}
