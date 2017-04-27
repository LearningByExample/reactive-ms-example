package org.learning.by.example.reactive.microservices.routers;

import org.learning.by.example.reactive.microservices.handlers.ErrorHandler;
import org.learning.by.example.reactive.microservices.handlers.HelloHandler;
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

    private static final String HELLO_PATH = "/hello";
    private static final String NAME_ARG = "{name}";

    @Bean
    RouterFunction<?> helloRouterFunction(final HelloHandler handler, final ErrorHandler errorHandler) {
        return nest(path(HELLO_PATH),
                nest(accept(APPLICATION_JSON),
                        route(GET("/"), handler::defaultHello)
                                .andRoute(POST("/"), handler::postHello)
                                .andRoute(GET("/"+NAME_ARG), handler::getHello)
                )).andOther(route(RequestPredicates.all(), errorHandler::notFound));
    }
}
