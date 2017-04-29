package org.learning.by.example.reactive.microservices.routers;

import org.learning.by.example.reactive.microservices.handlers.ErrorHandler;
import org.learning.by.example.reactive.microservices.handlers.ApiHandler;
import org.springframework.web.reactive.function.server.RouterFunction;

public class MainRouter {

    public static RouterFunction<?> doRoute(final ApiHandler handler, final ErrorHandler errorHandler) {
        return ApiRouter.doRoute(handler, errorHandler)
                .andOther(StaticRouter.doRoute());
    }
}
