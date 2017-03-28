package com.juan.medina.microservices;

import com.juan.medina.microservices.model.HelloRequest;
import com.juan.medina.microservices.model.HelloResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.test.StepVerifier;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ReactiveMsExampleApplicationTests {

    private static final String DEFAULT_VALUE = "world";
    private static final String CUSTOM_VALUE = "reactive";
    private static final String HELLO_PATH = "/hello";
    private static final String NAME_ARG = "{name}";

    private WebTestClient client;

    @LocalServerPort
    private
    int port;

    @Before
    public void setup() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    public void defaultHelloTest() {

        FluxExchangeResult<HelloResponse> result = client.get()
                .uri(HELLO_PATH)
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(HelloResponse.class)
                .returnResult();

        StepVerifier.create(result.getResponseBody())
                .consumeNextWith(p ->
                        assertThat(p.getHello(), is(DEFAULT_VALUE)
                        ))
                .thenCancel()
                .verify();
    }

    @Test
    public void getHelloTest() {

        FluxExchangeResult<HelloResponse> result = client.get()
                .uri(builder -> builder.path(HELLO_PATH)
                .path("/").path(NAME_ARG).build(CUSTOM_VALUE))
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(HelloResponse.class)
                .returnResult();

        StepVerifier.create(result.getResponseBody())
                .consumeNextWith(it ->
                        assertThat(it.getHello(), is(CUSTOM_VALUE)
                        ))
                .thenCancel()
                .verify();
    }

    @Test
    public void postHelloTest() {

        FluxExchangeResult<HelloResponse> result = client.post()
                .uri(HELLO_PATH)
                .accept(APPLICATION_JSON_UTF8)
                .exchange(BodyInserters.fromObject(new HelloRequest(CUSTOM_VALUE)))
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(HelloResponse.class)
                .returnResult();

        StepVerifier.create(result.getResponseBody())
                .consumeNextWith(it ->
                        assertThat(it.getHello(), is(CUSTOM_VALUE)
                        ))
                .thenCancel()
                .verify();

    }

}
