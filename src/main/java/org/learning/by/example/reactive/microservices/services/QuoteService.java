package org.learning.by.example.reactive.microservices.services;


import org.learning.by.example.reactive.microservices.model.Quote;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

public interface QuoteService {
    Supplier<Mono<Quote>> getQuote();
}
