package org.learning.by.example.reactive.microservices.services;


import org.learning.by.example.reactive.microservices.model.Quote;
import reactor.core.publisher.Mono;

public interface QuoteService {
    Mono<Quote> getQuote();
}
