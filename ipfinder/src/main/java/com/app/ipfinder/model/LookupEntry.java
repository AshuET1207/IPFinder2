package com.app.ipfinder.model;

import java.time.Instant;

public record LookupEntry(
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
        String asInfo,
        Instant timestamp
) {
}