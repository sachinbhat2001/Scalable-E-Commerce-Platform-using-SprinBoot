package com.payment.payment_service.config;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WebClientService {
    
    private final WebClient webClient;

    public WebClientService() {
        this.webClient = WebClient.create();
    }

    public <T, R> R put(String url, T requestBody, Class<R> responseType) {
        return webClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}