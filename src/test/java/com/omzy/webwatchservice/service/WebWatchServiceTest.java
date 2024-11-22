package com.omzy.webwatchservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
class WebWatchServiceTest {

    @InjectMocks
    @Spy
    private WebWatchService urlPingService;

    private HttpURLConnection mockConnection;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        mockConnection = mock(HttpURLConnection.class);
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void should_ping_url_up() throws Exception {
        // Given
        when(mockConnection.getResponseCode()).thenReturn(200);
        doReturn(mockConnection).when(urlPingService).createConnection(anyString());

        // When
        boolean result = urlPingService.pingUrl("http://example.com");

        // Then
        assertTrue(result);
    }

    @Test
    public void should_ping_url_down() throws Exception {
        // Given
        when(mockConnection.getResponseCode()).thenReturn(404);
        doReturn(mockConnection).when(urlPingService).createConnection(anyString());

        // When
        boolean result = urlPingService.pingUrl("http://example.com");

        // Then
        assertFalse(result);
    }

    @Test
    public void should_ping_url_error() throws Exception {
        // Given
        doThrow(new IOException()).when(urlPingService).createConnection(anyString());

        // When
        urlPingService.pingUrl("http://example.com");

        // Then
        assertTrue(outContent.toString().contains("Error pinging site"));
    }
}
