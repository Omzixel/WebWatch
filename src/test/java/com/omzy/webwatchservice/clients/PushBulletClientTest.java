package com.omzy.webwatchservice.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@Slf4j
class PushBulletClientTest {

    private final String pushBulletToken = "mock-token";
    private final PushBulletClient pushBulletClient = new PushBulletClient();
    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();

        WebClient webClient = WebClient.builder().baseUrl(wireMockServer.baseUrl()).build();

        ReflectionTestUtils.setField(pushBulletClient, "pushBulletToken", pushBulletToken);
        ReflectionTestUtils.setField(pushBulletClient, "pushBulletUrl", wireMockServer.baseUrl());
        ReflectionTestUtils.setField(pushBulletClient, "webClient", webClient);
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Should send a notification successfully")
    void sendNotification_SuccessResponse() {

        // Given
        wireMockServer.stubFor(post(urlEqualTo("/"))
                .withHeader("Authorization", equalTo("Bearer mock-token"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.title", equalTo("Test Title")))
                .withRequestBody(matchingJsonPath("$.body", equalTo("Test Message")))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                            "title": "Test Title",
                            "body": "Test Message"
                        }
                        """)));


        // When
        pushBulletClient.sendNotification("Test Title", "Test Message");

        // Then
        wireMockServer.verify(postRequestedFor(urlEqualTo("/"))
                .withHeader("Authorization", equalTo("Bearer " + pushBulletToken))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.title", equalTo("Test Title")))
                .withRequestBody(matchingJsonPath("$.body", equalTo("Test Message"))));
    }
}
