package ru.offer.hunt.learning.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.offer.hunt.learning.model.dto.*;

@Component
public class AiIntegrationClient {
    private final RestClient restClient;

    public AiIntegrationClient(@Value("${app.services.ai-integration.url}") String aiServiceUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);

        this.restClient = RestClient.builder()
                .baseUrl(aiServiceUrl)
                .requestFactory(factory)
                .build();
    }

    public AiResponseDto explainConcept(AiExternalExplainRequest request) {
        return restClient.post()
                .uri("/ai/explain/concept")
                .body(request)
                .retrieve()
                .body(AiResponseDto.class);
    }

    public AiResponseDto getHint(AiExternalHintRequest request) {
        return restClient.post()
                .uri("/ai/assist/hint")
                .body(request)
                .retrieve()
                .body(AiResponseDto.class);
    }
}