package org.learning.by.example.reactive.microservices.test;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.function.Function;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.TEXT_HTML;

public abstract class BasicIntegrationTest {
    protected WebTestClient client;

    protected void bindToRouterFunction(RouterFunction<?> routerFunction) {
        client = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    protected void bindToServerPort(int port) {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    protected String get(final Function<UriBuilder, URI> builder) {
        return get(builder, HttpStatus.OK);
    }

    protected String get(final Function<UriBuilder, URI> builder, final HttpStatus status) {
        return new String(client.get()
                .uri(builder)
                .accept(TEXT_HTML).exchange()
                .expectStatus().isEqualTo(status)
                .expectHeader().contentType(TEXT_HTML)
                .expectBody().returnResult().getResponseBody());
    }

    protected <T> T get(final Function<UriBuilder, URI> builder, final HttpStatus status, final Class<T> type) {
        return client.get()
                .uri(builder)
                .accept(APPLICATION_JSON_UTF8).exchange()
                .expectStatus().isEqualTo(status)
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(type)
                .returnResult().getResponseBody();
    }

    protected <T> T get(final Function<UriBuilder, URI> builder, final Class<T> type) {
        return get(builder, HttpStatus.OK, type);
    }

    protected <T, K> T post(final Function<UriBuilder, URI> builder, final HttpStatus status, final K object, final Class<T> type) {
        return client.post()
                .uri(builder)
                .body(BodyInserters.fromObject(object))
                .accept(APPLICATION_JSON_UTF8).exchange()
                .expectStatus().isEqualTo(status)
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(type)
                .returnResult().getResponseBody();
    }

    protected <T, K> T post(final Function<UriBuilder, URI> builder, final K object, final Class<T> type) {
        return post(builder, HttpStatus.OK, object, type);
    }
}
