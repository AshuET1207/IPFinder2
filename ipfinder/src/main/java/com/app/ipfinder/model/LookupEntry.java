package com.app.ipfinder.model;

import java.time.Instant;

public record LookupEntry(
        String ip,
        String country,
        String city,
        String isp,
        Instant timestamp
) {
}