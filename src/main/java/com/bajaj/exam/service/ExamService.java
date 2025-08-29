package com.yourcompany.exam.service;

import com.yourcompany.exam.model.SolutionRequest;
import com.yourcompany.exam.model.WebhookRequest;
import com.yourcompany.exam.model.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ExamService {

    private static final Logger log = LoggerFactory.getLogger(ExamService.class);
    private final WebClient webClient;
    private static final String BASE_URL = "https://bfhldevapigw.healthrx.co.in/hiring";

    public ExamService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    public Mono<WebhookResponse> generateWebhook(WebhookRequest request) {
        log.info("Sending initial request to generate webhook...");
        return webClient.post()
                .uri("/generateWebhook/JAVA")
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    log.error("Client error received when generating webhook: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Client error: " + response.statusCode()));
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    log.error("Server error received when generating webhook: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Server error: " + response.statusCode()));
                })
                .bodyToMono(WebhookResponse.class)
                .doOnSuccess(response -> log.info("Successfully received webhook and token."));
    }

    public Mono<Void> submitSolution(String webhookUrl, String accessToken, SolutionRequest request) {
        log.info("Submitting solution to the webhook...");
        return webClient.post()
                .uri(webhookUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    log.error("Client error received when submitting solution: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Client error: " + response.statusCode()));
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    log.error("Server error received when submitting solution: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Server error: " + response.statusCode()));
                })
                .bodyToMono(Void.class)
                .doOnSuccess(voids -> log.info("Solution submitted successfully."));
    }
}