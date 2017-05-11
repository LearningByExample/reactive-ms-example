package org.learning.by.example.reactive.microservices.test.categories;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.learning.by.example.reactive.microservices.application.ReactiveMsApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Tag("UnitTest")
@SpringBootTest(classes = ReactiveMsApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public @interface UnitTest {
}
