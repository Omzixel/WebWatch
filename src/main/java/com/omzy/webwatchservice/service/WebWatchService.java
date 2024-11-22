package com.omzy.webwatchservice.service;

import com.omzy.webwatchservice.clients.PushBulletClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final PushBulletClient pushBulletClient;

    public WebWatchService(PushBulletClient pushBulletClient) {
        this.pushBulletClient = pushBulletClient;
    }

    @Scheduled(cron = "0 */30 * * * *")
    void pingWebSiteJob() {
        boolean isUp = pingUrl(URL);

        if (!isUp) {
            pushBulletClient.sendNotification("Website Status Alert", "The website is currently unreachable.");
        }
    }

    public boolean pingUrl(String url) {
        try {
            HttpURLConnection connection = createConnection(url);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                log.info("Website is unreachable");
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            log.error("Error pinging site");
            return false;
        }
    }

    protected HttpURLConnection createConnection(String url) throws IOException {
        URL siteURL = new URL(url);
        return (HttpURLConnection) siteURL.openConnection();
    }

}
