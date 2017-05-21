package org.learning.by.example.reactive.microservices.application;

import org.learning.by.example.reactive.microservices.handlers.ApiHandler;
import org.learning.by.example.reactive.microservices.handlers.ErrorHandler;
import org.learning.by.example.reactive.microservices.routers.MainRouter;
import org.learning.by.example.reactive.microservices.services.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;

@Configuration
@EnableWebFlux
public class ApplicationConfig {

    @Bean
    ApiHandler apiHandler(final GeoLocationService geoLocationService, final SunriseSunsetService sunriseSunsetService,
                          final ErrorHandler errorHandler) {
        return new ApiHandler(geoLocationService, sunriseSunsetService, errorHandler);
    }

    @Bean
    GeoLocationService locationService(@Value("${GeoLocationServiceImpl.endPoint}") final String endPoint) {
        return new GeoLocationServiceImpl(endPoint);
    }

    @Bean
    SunriseSunsetService sunriseSunsetService(@Value("${SunriseSunsetServiceImpl.endPoint}") final String endPoint) {
        return new SunriseSunsetServiceImpl(endPoint);
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
