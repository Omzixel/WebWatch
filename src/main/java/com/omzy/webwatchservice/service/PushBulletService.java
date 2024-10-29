package com.omzy.webwatchservice.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class PushBulletService {

    //TODO fix variables (ENV), implemente better logging and make TESTS
    private static final String PUSHBULLET_API_URL = "url";
    private static final String ACCESS_TOKEN = "token";

    private final WebClient webClient;

    public PushBulletService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(PUSHBULLET_API_URL).build();
    }

    public void sendNotification(String title, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("type", "note");
        body.put("title", title);
        body.put("body", message);

        webClient.post()
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(WebClientResponseException.class, ex -> {
                    System.err.println("Error response from Pushbullet: " + ex.getResponseBodyAsString());
                })
                .subscribe(response -> System.out.println("Pushbullet Response: " + response));
    }

}
