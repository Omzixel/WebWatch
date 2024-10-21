package com.omzy.webwatchservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
@EnableScheduling
public class WebWatchService {

    @Value("${website.url}")
    public String URL;

    @Scheduled(cron = "0 */30 * * * *")
    void TestFunction(){
        pingUrl(URL);
    }

    public void pingUrl(String url) {
        try {
            HttpURLConnection connection = createConnection(url);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                log.info("UP");
            } else {
                log.info("DOWN");
            }
        } catch (IOException e) {
            log.info("Error pinging site");
        }
    }

    protected HttpURLConnection createConnection(String url) throws IOException {
        URL siteURL = new URL(url);
        return (HttpURLConnection) siteURL.openConnection();
    }

}
