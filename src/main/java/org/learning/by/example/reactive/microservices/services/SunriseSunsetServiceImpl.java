package org.learning.by.example.reactive.microservices.services;

import org.springframework.web.reactive.function.client.WebClient;

public class SunriseSunsetServiceImpl implements SunriseSunsetService{

    WebClient webClient;
    private final String endPoint;

    public SunriseSunsetServiceImpl(final String endPoint){
        this.endPoint = endPoint;
        this.webClient = WebClient.create();
    }
}
