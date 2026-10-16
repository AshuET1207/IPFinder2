package com.app.ipfinder.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.SerializationFeature;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableConfigurationProperties(IpFinderProperties.class)
public class AppConfig {

    /** Background pool for network lookups and file writes (never the FX thread). */
    @Bean(destroyMethod = "shutdownNow")
    public ExecutorService backgroundExecutor() {
        AtomicInteger counter = new AtomicInteger();
        return Executors.newFixedThreadPool(4, runnable -> {
            Thread thread = new Thread(runnable, "ipfinder-worker-" + counter.incrementAndGet());
            thread.setDaemon(true); // never keeps the JVM alive after the window closes
            return thread;
        });
    }

    /** Shared JDK HTTP client used by the GeoIpClient implementation. */
    @Bean
    public HttpClient httpClient(IpFinderProperties props, ExecutorService backgroundExecutor) {
        return HttpClient.newBuilder()
                .connectTimeout(props.api().timeout())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .executor(backgroundExecutor)
                .build();
    }

    /** Pretty-print JSON so history.json is readable and diff-friendly. */
    @Bean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.enable(SerializationFeature.INDENT_OUTPUT);
    }
}