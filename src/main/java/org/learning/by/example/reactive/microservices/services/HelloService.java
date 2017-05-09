package org.learning.by.example.reactive.microservices.services;

import reactor.core.publisher.Mono;

public interface HelloService {
    Mono<String> greetings(Mono<String> name);
}
