package com.omzy.webwatchservice.clients;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PushBulletClient {

    @Value("${pushbullet.token}")
    public String pushBulletToken;

    @Value("${pushbullet.url}")
    public String pushBulletUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(pushBulletUrl)
                .build();
    }

    public void sendNotification(String title, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("type", "note");
        body.put("title", title);
        body.put("body", message);

        webClient.post()
                .header("Authorization", "Bearer " + pushBulletToken)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("Error response from Pushbullet: " + ex.getResponseBodyAsString());
                })
                .block();
    }

}
