package org.learning.by.example.reactive.microservices.application;

import org.learning.by.example.reactive.microservices.handlers.ApiHandler;
import org.learning.by.example.reactive.microservices.handlers.ErrorHandler;
import org.learning.by.example.reactive.microservices.routers.MainRouter;
import org.learning.by.example.reactive.microservices.services.HelloService;
import org.learning.by.example.reactive.microservices.services.HelloServiceImpl;
import org.learning.by.example.reactive.microservices.services.QuoteService;
import org.learning.by.example.reactive.microservices.services.QuoteServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;

@Configuration
@EnableWebFlux
public class ApplicationConfig {

    @Bean
    QuoteService quoteService(@Value("${QuoteServiceImpl.endPoint}") final String endPoint){
        return new QuoteServiceImpl(endPoint);
    }

    @Bean
    HelloService helloService() {
        return new HelloServiceImpl();
    }

    @Bean
    ApiHandler apiHandler(final HelloService helloService, final QuoteService quoteService,
                          final ErrorHandler errorHandler) {
        return new ApiHandler(helloService, quoteService, errorHandler);
    }

    @Bean
    ErrorHandler errorHandler() {
        return new ErrorHandler();
    }

    @Bean
    RouterFunction<?> mainRouterFunction(final ApiHandler apiHandler, final ErrorHandler errorHandler) {
        return MainRouter.doRoute(apiHandler, errorHandler);
    }
}
