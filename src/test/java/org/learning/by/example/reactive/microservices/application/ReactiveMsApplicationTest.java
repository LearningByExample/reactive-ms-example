package org.learning.by.example.reactive.microservices.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.learning.by.example.reactive.microservices.test.BasicIntegrationTest;
import org.learning.by.example.reactive.microservices.test.categories.SystemTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = ReactiveMsApplication.class)
@ActiveProfiles("test")
@SystemTest
@DisplayName("ApplicationTest System Tests")
class ReactiveMsApplicationTest extends BasicIntegrationTest {

    private static final String DEFAULT_VALUE = "world";
    private static final String CUSTOM_VALUE = "reactive";
    private static final String JSON_VALUE = "json";
    private static final String HELLO_PATH = "/api/hello";
    private static final String NAME_ARG = "{name}";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup(){
        bindToServerPort(port);
    }

    @Test
    @DisplayName("get default")
    void defaultHelloTest(){
        final HelloResponse response = get(
                builder -> builder.path(HELLO_PATH).build(),
                HelloResponse.class);

        assertThat(response.getGreetings(), is(DEFAULT_VALUE));
        assertThat(response.getQuote(), not(isEmptyOrNullString()));
    }

    @Test
    @DisplayName("get variable")
    void getHelloTest() {
        final HelloResponse response = get(
                builder -> builder.path(HELLO_PATH).path("/").path(NAME_ARG).build(CUSTOM_VALUE),
                HelloResponse.class);

        assertThat(response.getGreetings(), is(CUSTOM_VALUE));
        assertThat(response.getQuote(), not(isEmptyOrNullString()));
    }

    @Test
    @DisplayName("post json")
    void postHelloTest() {
        final HelloResponse response = post(
                builder -> builder.path(HELLO_PATH).build(),
                new HelloRequest(JSON_VALUE),
                HelloResponse.class);

        assertThat(response.getGreetings(), is(JSON_VALUE));
        assertThat(response.getQuote(), not(isEmptyOrNullString()));
    }
}
