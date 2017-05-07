package org.learning.by.example.reactive.microservices;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.learning.by.example.reactive.microservices.test.BasicIntegrationTest;
import org.learning.by.example.reactive.microservices.test.categories.SystemTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Category(SystemTest.class)
public class ReactiveMsApplicationTest extends BasicIntegrationTest {

    private static final String DEFAULT_VALUE = "world";
    private static final String CUSTOM_VALUE = "reactive";
    private static final String JSON_VALUE = "json";
    private static final String HELLO_PATH = "/api/hello";
    private static final String NAME_ARG = "{name}";

    @LocalServerPort
    private int port;

    @Before
    public void setup(){
        bindToServerPort(port);
    }

    @Test
    public void defaultHelloTest(){
        final HelloResponse response = get(
                builder -> builder.path(HELLO_PATH).build(),
                HelloResponse.class);

        assertThat(response.getGreetings(), is(DEFAULT_VALUE));
        assertThat(response.getQuote(), not(isEmptyOrNullString()));
    }

    @Test
    public void getHelloTest() {
        final HelloResponse response = get(
                builder -> builder.path(HELLO_PATH).path("/").path(NAME_ARG).build(CUSTOM_VALUE),
                HelloResponse.class);

        assertThat(response.getGreetings(), is(CUSTOM_VALUE));
        assertThat(response.getQuote(), not(isEmptyOrNullString()));
    }

    @Test
    public void postHelloTest() {
        final HelloResponse response = post(
                builder -> builder.path(HELLO_PATH).build(),
                new HelloRequest(JSON_VALUE),
                HelloResponse.class);

        assertThat(response.getGreetings(), is(JSON_VALUE));
        assertThat(response.getQuote(), not(isEmptyOrNullString()));
    }
}
