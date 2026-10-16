package com.app.ipfinder.service;

import com.app.ipfinder.client.GeoIpClient;
import com.app.ipfinder.model.IpInfo;
import com.app.ipfinder.model.LookupEntry;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class IpLookupService {

    private final GeoIpClient geoIpClient;
    private final LookupQueryValidator ipValidator;
    private final HistoryService historyService;

    public IpLookupService(
            GeoIpClient geoIpClient,
            LookupQueryValidator ipValidator,
            HistoryService historyService) {
        this.geoIpClient = geoIpClient;
        this.ipValidator = ipValidator;
        this.historyService = historyService;
    }

    public LookupEntry lookup(String query) {
        ipValidator.validate(query);

        String normalizedQuery = query.trim();

        IpInfo info = geoIpClient.lookup(normalizedQuery);

        LookupEntry entry = new LookupEntry(
                info.query() != null ? info.query() : normalizedQuery,
                info.country(),
                info.countryCode(),
                info.regionName(),
                info.city(),
                info.zip(),
                info.lat(),
                info.lon(),
                info.timezone(),
                info.isp(),
                info.org(),
                info.as(),
                Instant.now());

        historyService.add(entry);

        return entry;
    }
}
