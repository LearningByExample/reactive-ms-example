package org.learning.by.example.reactive.microservices.services;

import org.learning.by.example.reactive.microservices.exceptions.GetQuoteException;
import org.learning.by.example.reactive.microservices.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Supplier;

public class QuoteServiceImpl implements QuoteService {

    private static final String ERROR_GETTING_QUOTE = "ERROR GETTING_QUOTE";
    private final String endPoint;
    private Logger logger = LoggerFactory.getLogger(QuoteService.class);

    private final WebClient webClient;

    public QuoteServiceImpl(final String endPoint) {
        this.endPoint = endPoint;
        webClient = WebClient.create();
    }

    Supplier<Mono<Quote[]>> request() {
        return () -> webClient
                .get()
                .uri(endPoint)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Quote[].class));
    }

    Function<Mono<Quote[]>, Mono<Quote>> chooseFirst() {
        return mono -> mono.flatMap(quotes -> Mono.just(quotes[0]))
                .onErrorResume(throwable ->Mono.error(new GetQuoteException(ERROR_GETTING_QUOTE, throwable)));
    }

    @Override
    public Supplier<Mono<Quote>> getQuote() {
        return () -> Mono.defer(request()).publish(chooseFirst())
                .onErrorResume(throwable ->Mono.error(new GetQuoteException(ERROR_GETTING_QUOTE, throwable)));
    }

}
