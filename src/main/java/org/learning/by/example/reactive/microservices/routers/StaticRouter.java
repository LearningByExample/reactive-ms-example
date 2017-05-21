package org.learning.by.example.reactive.microservices.routers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RouterFunctions.resources;

class StaticRouter {

    private static final String ROUTE = "/**";
    private static final String PUBLIC = "public/";

    static RouterFunction<?> doRoute() {
        return resources(ROUTE, new ClassPathResource(PUBLIC));
    }
}
