package org.learning.by.example.reactive.microservices.routers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.model.ErrorResponse;
import org.learning.by.example.reactive.microservices.model.HelloRequest;
import org.learning.by.example.reactive.microservices.model.HelloResponse;
import org.learning.by.example.reactive.microservices.model.WrongRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.IsNot.not;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppRouterTests extends BasicRouterTest {

    private static final String DEFAULT_VALUE = "world";
    private static final String CUSTOM_VALUE = "reactive";
    private static final String JSON_VALUE = "json";
    private static final String HELLO_PATH = "/api/hello";
    private static final String NAME_ARG = "{name}";
    private static final String WRONG_PATH = "/api/wrong";
    private static final String STATIC_PATH = "/index.html";
    private static final String HELLO_WORLD_FROM_WEB_FLUX = "Hello World : From web-flux";

    @Autowired
    private RouterFunction<?> appRouterFunction;

    @Before
    public void setup() {
        super.setup(appRouterFunction);
    }

    @Test
    public void defaultHelloTest() {
        final HelloResponse response = get(
                builder -> builder.path(HELLO_PATH).build(),
                HelloResponse.class);

        assertThat(response.getHello(), is(DEFAULT_VALUE));
    }

    @Test
    public void getHelloTest() {
        final HelloResponse response = get(
                builder -> builder.path(HELLO_PATH).path("/").path(NAME_ARG).build(CUSTOM_VALUE),
                HelloResponse.class);

        assertThat(response.getHello(), is(CUSTOM_VALUE));
    }

    @Test
    public void postHelloTest() {
        final HelloResponse response = post(
                builder -> builder.path(HELLO_PATH).build(),
                new HelloRequest(JSON_VALUE),
                HelloResponse.class);

        assertThat(response.getHello(), is(JSON_VALUE));
    }

    @Test
    public void getWrongPath() {
        final ErrorResponse response = get(
                builder -> builder.path(WRONG_PATH).build(),
                HttpStatus.NOT_FOUND,
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

    @Test
    public void postWrongPath() {
        final ErrorResponse response = post(
                builder -> builder.path(WRONG_PATH).build(),
                HttpStatus.NOT_FOUND,
                new HelloRequest(JSON_VALUE),
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

    @Test
    public void postWrongObject() {
        final ErrorResponse response = post(
                builder -> builder.path(HELLO_PATH).build(),
                HttpStatus.BAD_REQUEST,
                new WrongRequest(JSON_VALUE),
                ErrorResponse.class);

        assertThat(response.getError(), not(isEmptyOrNullString()));
    }

    @Test
    public void staticContent(){
        String result = get(
                builder -> builder.path(STATIC_PATH).build(),
                HttpStatus.OK
        );
        assertThat(result, not(isEmptyOrNullString()));
        verifyTitleIs(result, HELLO_WORLD_FROM_WEB_FLUX);
    }

    private void verifyTitleIs(final String html, final String title){
        Document doc = Jsoup.parse(html);
        Element element = doc.head().getElementsByTag("title").get(0);
        String text = element.text();
        assertThat(text, is(title));
    }

}
