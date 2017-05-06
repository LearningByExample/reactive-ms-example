package org.learning.by.example.reactive.microservices.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.exceptions.GetQuoteException;
import org.learning.by.example.reactive.microservices.model.Quote;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuoteServiceImplTest {

    private static final String BAD_EXCEPTION = "BAD_EXCEPTION";
    private static final String SHOULD_NOT_RETURN_OBJECT = "Shouldn't get a object";

    private static final int MOCK_ID = 100;
    private static final String MOCK_TITLE = "title";
    private static final String MOCK_LINK = "link";
    private static final String MOCK_CONTENT = "content";

    @SpyBean
    private QuoteServiceImpl quoteService;

    private Mono<Quote[]> createMockedResponse(final Integer ID, final String title, final String link,
                                               final String content) {
        Quote quote = new Quote();

        quote.setID(ID);
        quote.setTitle(title);
        quote.setLink(link);
        quote.setContent(content);

        return Mono.just(new Quote[]{quote});
    }

    @Test
    public void mockedRequest() {
        given(quoteService.requestQuotes()).willReturn(
                createMockedResponse(MOCK_ID, MOCK_TITLE, MOCK_LINK, MOCK_CONTENT)
        );

        quoteService.getQuote().subscribe(quote -> {
            assertThat(quote.getID(), is(MOCK_ID));
            assertThat(quote.getTitle(), is(MOCK_TITLE));
            assertThat(quote.getLink(), is(MOCK_LINK));
            assertThat(quote.getContent(), is(MOCK_CONTENT));
        });

        reset(quoteService);
    }

    @Test
    public void requestErrorShouldBeHandle() {
        given(quoteService.requestQuotes()).willReturn(Mono.error(new RuntimeException(BAD_EXCEPTION)));

        quoteService.getQuote().subscribe(quote -> {
            throw new UnsupportedOperationException(SHOULD_NOT_RETURN_OBJECT);
        }, throwable -> {
            assertThat(throwable, instanceOf(GetQuoteException.class));
        });

        reset(quoteService);
    }

    @Test
    public void chooseFirstErrorShouldBeHandle() {
        given(quoteService.chooseFirst()).willReturn(mono -> Mono.error(new RuntimeException(BAD_EXCEPTION)));

        quoteService.getQuote().subscribe(quote -> {
            throw new UnsupportedOperationException(SHOULD_NOT_RETURN_OBJECT);
        }, throwable -> {
            assertThat(throwable, instanceOf(GetQuoteException.class));
        });

        reset(quoteService);
    }

}
