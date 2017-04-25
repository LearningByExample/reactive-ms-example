package org.learning.by.example.reactive.microservices;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class HelloRouter {
    @Bean
    RouterFunction<?> helloRouterFunction() {
        HelloHandler handler = new HelloHandler();
        ErrorHandler error = new ErrorHandler();

        return nest(path("/hello"),
                nest(accept(APPLICATION_JSON),
                        route(GET("/"), handler::defaultHello)
                                .andRoute(POST("/"), handler::postHello)
                                .andRoute(GET("/resource/{name}"), handler::getHello)
                                .andRoute(GET("/web/"), handler::getWebsite)
                )).andOther(route(RequestPredicates.all(), error::notFound));
    }
}
