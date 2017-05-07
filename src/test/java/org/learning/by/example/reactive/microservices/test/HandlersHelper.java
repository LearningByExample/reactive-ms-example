package org.learning.by.example.reactive.microservices.test;

import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class HandlersHelper {
    @SuppressWarnings("unchecked")
    public static <T> T extractEntity(ServerResponse response, Class<T> type) {

        EntityResponse<Mono<T>> entityResponse = (EntityResponse<Mono<T>>) response;

        return type.cast(entityResponse.entity().block());
    }
}
