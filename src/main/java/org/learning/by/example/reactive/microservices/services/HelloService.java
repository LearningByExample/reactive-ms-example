package org.learning.by.example.reactive.microservices.services;

import org.learning.by.example.reactive.microservices.exceptions.InvalidParametersException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
public class HelloService {
    public Function<Mono<String>, Mono<String>> getGreetings() {
        return value -> value.flatMap(name -> {
            if (name.equals("")) {
                return Mono.error(new InvalidParametersException("bad parameters"));
            }
            return Mono.just(name);
        });
    }
}
