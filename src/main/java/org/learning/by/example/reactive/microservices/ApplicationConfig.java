package org.learning.by.example.reactive.microservices;

import org.learning.by.example.reactive.microservices.handlers.ErrorHandler;
import org.learning.by.example.reactive.microservices.handlers.HelloHandler;
import org.learning.by.example.reactive.microservices.routers.HelloRouter;
import org.learning.by.example.reactive.microservices.services.HelloService;
import org.learning.by.example.reactive.microservices.services.HelloServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;

@Configuration
@EnableWebFlux
public class ApplicationConfig {

    @Bean
    HelloService helloService() {
        return new HelloServiceImpl();
    }

    @Bean
    HelloHandler helloHandler(final HelloService helloService, final ErrorHandler errorHandler) {
        return new HelloHandler(helloService, errorHandler);
    }

    @Bean
    ErrorHandler errorHandler() {
        return new ErrorHandler();
    }

    @Bean
    RouterFunction<?> helloRouterFunction(final HelloHandler handler, final ErrorHandler errorHandler) {
        return HelloRouter.doRoute(handler, errorHandler);
    }
}
