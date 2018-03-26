package org.learning.by.example.reactive.microservices.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RestServiceHelper {
    private static <T> Mono<T> getMonoFromJson(final String json, final Class<T> type) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Mono.just(objectMapper.readValue(json, type));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Mono<T> getMonoFromJsonPath(final String jsonPath, final Class<T> type) {
        try {
            final URL url = RestServiceHelper.class.getResource(jsonPath);
            final Path resPath = java.nio.file.Paths.get(url.toURI());
            final String json = new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");
            return getMonoFromJson(json, type);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static WebClient mockWebClient(final WebClient originalClient, final Mono<?> mono){
        WebClient client = spy(originalClient);

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        doReturn(uriSpec).when(client).get();

        WebClient.RequestHeadersSpec<?> headerSpec = mock(WebClient.RequestHeadersSpec.class);
        doReturn(headerSpec).when(uriSpec).uri(anyString());
        doReturn(headerSpec).when(headerSpec).accept(any());

        ClientResponse clientResponse = mock(ClientResponse.class);
        doReturn(mono).when(clientResponse).bodyToMono((Class<?>) Mockito.any());
        doReturn(Mono.just(clientResponse)).when(headerSpec).exchange();

        return client;
    }
}
