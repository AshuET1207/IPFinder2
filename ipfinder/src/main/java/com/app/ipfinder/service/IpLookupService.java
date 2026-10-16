package com.app.ipfinder.service;

import com.app.ipfinder.client.GeoIpClient;
import com.app.ipfinder.model.IpInfo;
import com.app.ipfinder.model.LookupEntry;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class IpLookupService {

    private final GeoIpClient geoIpClient;
    private final IpValidator ipValidator;
    private final HistoryService historyService;

    public IpLookupService(
            GeoIpClient geoIpClient,
            IpValidator ipValidator,
            HistoryService historyService
    ) {
        this.geoIpClient = geoIpClient;
        this.ipValidator = ipValidator;
        this.historyService = historyService;
    }

    public LookupEntry lookup(String ip) {
        ipValidator.validate(ip);

        String normalizedIp = ip.trim();

        IpInfo info = geoIpClient.lookup(normalizedIp);

        LookupEntry entry = new LookupEntry(
                info.query() != null
                        ? info.query()
                        : normalizedIp,
                info.country(),
                info.city(),
                info.isp(),
                Instant.now()
        );

        historyService.add(entry);

        return entry;
    }
}
