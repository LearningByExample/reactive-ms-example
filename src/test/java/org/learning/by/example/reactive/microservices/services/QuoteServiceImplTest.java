package org.learning.by.example.reactive.microservices.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.exceptions.GetQuoteException;
import org.learning.by.example.reactive.microservices.model.Quote;
import org.learning.by.example.reactive.microservices.test.categories.UnitTest;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import reactor.core.publisher.Mono;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@UnitTest
@DisplayName("QuoteServiceImpl Unit Tests")
class QuoteServiceImplTest {

    private static final String BAD_EXCEPTION = "BAD_EXCEPTION";
    private static final String SHOULD_NOT_RETURN_OBJECT = "Shouldn't get a object";

    private static final int MOCK_ID = 100;
    private static final String MOCK_TITLE = "title";
    private static final String MOCK_LINK = "link";
    private static final String MOCK_CONTENT = "content";

    @SpyBean(QuoteService.class)
    private QuoteServiceImpl quoteService;

    @Test
    void mockedRequest() {
        doReturn(createMockedResponse(MOCK_ID, MOCK_TITLE, MOCK_LINK, MOCK_CONTENT)).when(quoteService).request();

        quoteService.get().subscribe(quote -> {
            assertThat(quote.getID(), is(MOCK_ID));
            assertThat(quote.getTitle(), is(MOCK_TITLE));
            assertThat(quote.getLink(), is(MOCK_LINK));
            assertThat(quote.getContent(), is(MOCK_CONTENT));
        });

        verify(quoteService, times(1)).get();
        verify(quoteService, times(1)).request();
        verify(quoteService, times(1)).chooseFirst(Mockito.any());

        reset(quoteService);
    }

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
    void requestErrorShouldBeHandle() {
        doReturn(Mono.error(new RuntimeException(BAD_EXCEPTION))).when(quoteService).request();

        quoteService.get().subscribe(quote -> {
            throw new UnsupportedOperationException(SHOULD_NOT_RETURN_OBJECT);
        }, throwable -> {
            assertThat(throwable, instanceOf(GetQuoteException.class));
        });

        verify(quoteService, times(1)).get();
        verify(quoteService, times(1)).request();
        verify(quoteService, times(1)).chooseFirst(Mockito.any());

        reset(quoteService);
    }

    @Test
    void chooseFirstErrorShouldBeHandle() {
        doReturn(Mono.error(new RuntimeException(BAD_EXCEPTION))).when(quoteService).chooseFirst(Mockito.any());

        quoteService.get().subscribe(quote -> {
            throw new UnsupportedOperationException(SHOULD_NOT_RETURN_OBJECT);
        }, throwable -> {
            assertThat(throwable, instanceOf(GetQuoteException.class));
        });

        verify(quoteService, times(1)).get();
        verify(quoteService, times(1)).request();
        verify(quoteService, times(1)).chooseFirst(Mockito.any());

        reset(quoteService);
    }

}
