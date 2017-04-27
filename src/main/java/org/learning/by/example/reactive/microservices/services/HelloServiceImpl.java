package org.learning.by.example.reactive.microservices.services;


import org.learning.by.example.reactive.microservices.exceptions.InvalidParametersException;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class HelloServiceImpl implements HelloService{
    private static final String EMPTY = "";
    private static final String BAD_PARAMETERS = "bad parameters";

    @Override
    public Function<Mono<String>, Mono<String>> getGreetings() {
        return value -> value.flatMap(name -> {
            if (name.equals(EMPTY)) {
                return Mono.error(new InvalidParametersException(BAD_PARAMETERS));
            }
            return Mono.just(name);
        });
    }
}
