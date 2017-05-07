package org.learning.by.example.reactive.microservices;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.learning.by.example.reactive.microservices.test.BasicIntegrationTest;
import org.learning.by.example.reactive.microservices.test.categories.SystemTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Category(SystemTest.class)
public class ReactiveMsApplicationTest extends BasicIntegrationTest {

    private static final String API_ROUTE = "/api/hello";

    @LocalServerPort
    private int port;

    @Before
    public void setup(){
        bindToServerPort(port);
    }

    @Test
    public void validTest(){
        get(
                builder -> builder.path(API_ROUTE).build(),
                String.class
        );
    }
}
