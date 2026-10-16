package com.app.ipfinder.client;

import com.app.ipfinder.config.IpFinderProperties;
import com.app.ipfinder.exception.LookupFailedException;
import com.app.ipfinder.model.IpInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class IpApiGeoIpClient implements GeoIpClient {

    /**
     * Ask the provider only for fields that are mapped.
     * This keeps responses small.
     */
    private static final String FIELDS =
            "status,message,country,countryCode,region,regionName,"
            + "city,zip,lat,lon,timezone,isp,org,as,query";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final Duration timeout;

    public IpApiGeoIpClient(
            HttpClient httpClient,
            ObjectMapper objectMapper,
            IpFinderProperties props
    ) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;

        String url = props.api().baseUrl();
        this.baseUrl = url.endsWith("/") ? url : url + "/";

        this.timeout = props.api().timeout();
    }

    @Override
    public IpInfo lookup(String ip) {
        HttpRequest request = HttpRequest.newBuilder(buildUri(ip))
                .timeout(timeout)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            return toIpInfo(response);

        } catch (JacksonException e) {
            throw new LookupFailedException(
                    "The lookup service returned an unreadable response.",
                    e
            );

        } catch (IOException e) {
            throw new LookupFailedException(
                    "Could not reach the lookup service. Check your connection.",
                    e
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new LookupFailedException(
                    "Lookup was cancelled.",
                    e
            );
        }
    }

    private URI buildUri(String ip) {
        String target = (ip == null || ip.isBlank())
                ? ""
                : URLEncoder.encode(
                        ip.trim(),
                        StandardCharsets.UTF_8
                );

        return URI.create(
                baseUrl + target + "?fields=" + FIELDS
        );
    }

    private IpInfo toIpInfo(HttpResponse<String> response)
            throws JacksonException {

        int status = response.statusCode();

        if (status == 429) {
            throw new LookupFailedException(
                    "Too many requests. Please wait a minute and try again."
            );
        }

        if (status != 200) {
            throw new LookupFailedException(
                    "The lookup service returned HTTP " + status + "."
            );
        }

        ApiResponse body =
                objectMapper.readValue(
                        response.body(),
                        ApiResponse.class
                );

        if (!"success".equalsIgnoreCase(body.status())) {
            String reason = body.message() != null
                    ? body.message()
                    : "unknown error";

            throw new LookupFailedException(
                    "Lookup failed: " + reason
            );
        }

        return new IpInfo(
                body.status(),
                body.message(),
                body.query(),
                body.country(),
                body.countryCode(),
                body.regionName(),
                body.city(),
                body.zip(),
                body.lat(),
                body.lon(),
                body.timezone(),
                body.isp(),
                body.org(),
                body.asInfo()
        );
    }

    /**
     * Provider-specific JSON shape.
     *
     * Private intentionally: no other part of the application
     * should depend on ip-api.com's response format.
     */
    @JsonIgnoreProperties (ignoreUnknown = true)
    private record ApiResponse(
            String status,
            String message,
            String query,
            String country,
            String countryCode,
            String regionName,
            String city,
            String zip,
            Double lat,
            Double lon,
            String timezone,
            String isp,
            String org,
            @JsonProperty ("as") String asInfo
    ) {
    }
}