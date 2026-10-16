package com.app.ipfinder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.nio.file.Path;
import java.time.Duration;

@ConfigurationProperties(prefix = "ipfinder")
public record IpFinderProperties(
        Api api,
        History history
) {

    public IpFinderProperties {
        if (api == null) {
            api = new Api(
                    "http://ip-api.com/json/",
                    Duration.ofSeconds(5)
            );
        }

        if (history == null) {
            history = new History(
                    defaultHistoryPath(),
                    200
            );
        }
    }

    private static Path defaultHistoryPath() {
        return Path.of(
                System.getProperty("user.home"),
                ".ipfinder",
                "history.json"
        );
    }

    public record Api(
            @DefaultValue("http://ip-api.com/json/")
            String baseUrl,

            @DefaultValue("5s")
            Duration timeout
    ) {
        public Api {
            if (baseUrl == null || baseUrl.isBlank()) {
                baseUrl = "http://ip-api.com/json/";
            }

            if (timeout == null || timeout.isZero() || timeout.isNegative()) {
                timeout = Duration.ofSeconds(5);
            }
        }
    }

    public record History(
            Path file,

            @DefaultValue("200")
            int maxEntries
    ) {
        public History {
            if (file == null) {
                file = defaultHistoryPath();
            }

            if (maxEntries <= 0) {
                maxEntries = 200;
            }
        }
    }
}