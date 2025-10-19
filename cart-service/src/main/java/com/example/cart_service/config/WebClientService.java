package com.example.cart_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WebClientService {
	
	 private final WebClient webClient;

	    public WebClientService() {
	        this.webClient = WebClient.create(); // Simple WebClient without any configuration
	    }
    /**
     * Generic POST method that takes URL and request body
     */
    public <T, R> R post(String url, T requestBody, Class<R> responseType) {
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    /**
     * Generic POST method with custom headers
     */
    public <T, R> R post(String url, T requestBody, Class<R> responseType, 
                        String headerName, String headerValue) {
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header(headerName, headerValue)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    /**
     * Generic GET method
     */
    public <R> R get(String url, Class<R> responseType) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    /**
     * Generic GET method with path variables
     */
    public <R> R get(String url, Class<R> responseType, Object... uriVariables) {
        return webClient.get()
                .uri(url, uriVariables)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    /**
     * Generic PUT method
     */
    public <T, R> R put(String url, T requestBody, Class<R> responseType) {
        return webClient.put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    /**
     * Generic DELETE method
     */
    public <R> R delete(String url, Class<R> responseType) {
        return webClient.delete()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

}
