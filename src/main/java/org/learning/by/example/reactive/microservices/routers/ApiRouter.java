package org.learning.by.example.reactive.microservices.routers;

import org.learning.by.example.reactive.microservices.handlers.ErrorHandler;
import org.learning.by.example.reactive.microservices.handlers.ApiHandler;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

class ApiRouter {
    private static final String API_PATH = "/api";
    private static final String HELLO_PATH = "/hello";
    private static final String LOCATION_PATH = "/location";
    private static final String NAME_ARG = "/{name}";
    private static final String ADDRESS_ARG = "/{address}";
    private static final String HELLO_WITH_NAME_PATH = HELLO_PATH + NAME_ARG;
    private static final String LOCATION_WITH_ADDRESS_PATH = LOCATION_PATH + ADDRESS_ARG;

    static RouterFunction<?> doRoute(final ApiHandler apiHandler, final ErrorHandler errorHandler) {
        return
            nest(path(API_PATH),
                nest(accept(APPLICATION_JSON),
                    route(GET(HELLO_PATH), apiHandler::defaultHello)
                    .andRoute(POST(HELLO_PATH), apiHandler::postHello)
                    .andRoute(GET(HELLO_WITH_NAME_PATH), apiHandler::getHello)
                    .andRoute(GET(LOCATION_WITH_ADDRESS_PATH), apiHandler::getLocation)
                ).andOther(route(RequestPredicates.all(), errorHandler::notFound))
            );
    }
}