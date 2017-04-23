package org.learning.by.example.reactive.microservices;

import org.learning.by.example.reactive.microservices.model.ErrorResponse;
import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.IsNot.not;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToRouterFunction;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReactiveMsExampleApplicationTests {

    private static final String DEFAULT_VALUE = "world";
    private static final String CUSTOM_VALUE = "reactive";
    private static final String JSON_VALUE = "json";
    private static final String HELLO_PATH = "/hello";
    private static final String NAME_ARG = "{name}";
    private static final String WRONG_PATH = "/wrong";

    private WebTestClient client;

    @Autowired
    private RouterFunction<?> helloRouterFunction;

    @Before
    public void setup() {
        client = bindToRouterFunction(helloRouterFunction).build();
    }


    private <T> T get(Function<UriBuilder, URI> builder, HttpStatus status, Class<T> type) {

        return client.get()
                .uri(builder)
                .accept(APPLICATION_JSON_UTF8).exchange()
                .expectStatus().isEqualTo(status)
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(type)
                .returnResult().getResponseBody();
    }

    private <T> T get(Function<UriBuilder, URI> builder, Class<T> type) {

        return get(builder, HttpStatus.OK, type);
    }

    private <T, K> T post(Function<UriBuilder,URI> builder, HttpStatus status, K object, Class<T> type) {

        return client.post()
                .uri(builder)
                .body(BodyInserters.fromObject(object))
                .accept(APPLICATION_JSON_UTF8).exchange()
                .expectStatus().isEqualTo(status)
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(type)
                .returnResult().getResponseBody();
    }

    private <T, K> T post(Function<UriBuilder,URI> builder, K object, Class<T> type) {

        return post(builder,HttpStatus.OK, object, type);
    }

    @Test
    public void defaultHelloTest() {

        HelloResponse response = get(
                builder -> builder.path(HELLO_PATH).build(),
                HelloResponse.class);

        assertThat(response.getHello(), is(DEFAULT_VALUE));
    }

    @Test
    public void getHelloTest() {

        HelloResponse response = get(
                builder -> builder.path(HELLO_PATH).path("/").path(NAME_ARG).build(CUSTOM_VALUE),
                HelloResponse.class);

        assertThat(response.getHello(), is(CUSTOM_VALUE));
    }

    @Test
    public void postHelloTest() {

        HelloResponse response = post(
                builder -> builder.path(HELLO_PATH).build(),
                new HelloRequest(JSON_VALUE),
                HelloResponse.class);

        assertThat(response.getHello(), is(JSON_VALUE));
    }

    @Test
    public void getWrongPath() {

        ErrorResponse response = get(
                builder -> builder.path(WRONG_PATH).build(),
                HttpStatus.NOT_FOUND,
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

    @Test
    public void postWrongPath() {

        ErrorResponse response = post(
                builder -> builder.path(WRONG_PATH).build(),
                HttpStatus.NOT_FOUND,
                new HelloRequest(JSON_VALUE),
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

    @Test
    public void postWrongObject() {

        ErrorResponse response = post(
                builder -> builder.path(HELLO_PATH).build(),
                HttpStatus.BAD_REQUEST,
                new WrongRequest(JSON_VALUE),
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

}
