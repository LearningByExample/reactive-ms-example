package org.learning.by.example.reactive.microservices.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.test.tags.UnitTest;

@UnitTest
@DisplayName("ReactiveMsApplication Unit Tests")
class ReactiveMsApplicationUnitTest {

    @Test
    void ReactiveMsApplication() {
        final String[] args = {};
        ReactiveMsApplication.main(args);
    }
}
