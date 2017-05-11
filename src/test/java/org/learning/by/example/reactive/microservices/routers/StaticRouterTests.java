package org.learning.by.example.reactive.microservices.routers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.reactive.microservices.test.BasicTest;
import org.learning.by.example.reactive.microservices.test.categories.IntegrationTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.IsNot.not;

@IntegrationTest
@DisplayName(" StaticRouter Integration Tests")
class StaticRouterTests extends BasicTest {

    private static final String STATIC_PATH = "/index.html";
    private static final String DEFAULT_TITLE = "Swagger UI";
    private static final String TITLE_TAG = "title";

    @BeforeEach
    void setup() {
        super.bindToRouterFunction(StaticRouter.doRoute());
        final StaticRouter staticRouter = new StaticRouter();
    }

    @Test
    void staticContentTest() {
        String result = get(builder -> builder.path(STATIC_PATH).build());
        assertThat(result, not(isEmptyOrNullString()));
        verifyTitleIs(result, DEFAULT_TITLE);
    }

    private void verifyTitleIs(final String html, final String title) {
        Document doc = Jsoup.parse(html);
        Element element = doc.head().getElementsByTag(TITLE_TAG).get(0);
        String text = element.text();
        assertThat(text, is(title));
    }
}
