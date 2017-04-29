package org.learning.by.example.reactive.microservices.routers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.test.BasicRouterTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.server.RouterFunction;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MainRouterTests extends BasicRouterTest {

    private static final String STATIC_ROUTE = "/index.html";
    private static final String API_ROUTE = "/api/hello";
    @Autowired
    private RouterFunction<?> mainRouterFunction;

    @Before
    public void setup() {
        super.setup(mainRouterFunction);
    }

    @Test
    public void staticRouteTest() {
        get(builder -> builder.path(STATIC_ROUTE).build());
    }

    @Test
    public void apiRouteTest() {
        get(
                builder -> builder.path(API_ROUTE).build(),
                String.class
        );
    }
}
