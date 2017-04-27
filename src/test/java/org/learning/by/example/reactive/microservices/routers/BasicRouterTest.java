package org.learning.by.example.reactive.microservices.routers;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.function.Function;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToRouterFunction;

class BasicRouterTest {
    private WebTestClient client;

    void setup(RouterFunction<?> routerFunction) {
        client = bindToRouterFunction(routerFunction).build();
    }

    <T> T get(final Function<UriBuilder, URI> builder, final HttpStatus status, final Class<T> type) {
        return client.get()
                .uri(builder)
                .accept(APPLICATION_JSON_UTF8).exchange()
                .expectStatus().isEqualTo(status)
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(type)
                .returnResult().getResponseBody();
    }

    <T> T get(final Function<UriBuilder, URI> builder, final Class<T> type) {
        return get(builder, HttpStatus.OK, type);
    }

    <T, K> T post(final Function<UriBuilder, URI> builder, final HttpStatus status, final K object, final Class<T> type) {
        return client.post()
                .uri(builder)
                .body(BodyInserters.fromObject(object))
                .accept(APPLICATION_JSON_UTF8).exchange()
                .expectStatus().isEqualTo(status)
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(type)
                .returnResult().getResponseBody();
    }

    <T, K> T post(final Function<UriBuilder, URI> builder, final K object, final Class<T> type) {
        return post(builder,HttpStatus.OK, object, type);
    }
}
