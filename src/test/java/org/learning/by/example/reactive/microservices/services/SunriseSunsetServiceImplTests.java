package org.learning.by.example.reactive.microservices.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.test.categories.UnitTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@UnitTest
@DisplayName("SunriseSunsetServiceImplTests Unit Tests")
class SunriseSunsetServiceImplTests {

    @SpyBean(SunriseSunsetService.class)
    private SunriseSunsetServiceImpl sunriseSunsetService;

    @Test
    void getBeamTest() {
        assertThat(sunriseSunsetService, is(notNullValue()));
    }
}
