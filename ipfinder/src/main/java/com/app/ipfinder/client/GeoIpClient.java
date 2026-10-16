package com.app.ipfinder.client;

import com.app.ipfinder.exception.LookupFailedException;
import com.app.ipfinder.model.IpInfo;

public interface GeoIpClient {

    /**
     * Blocking call: run it from a background thread, never the JavaFX Application Thread.
     *
     * @param ip an IPv4/IPv6 address, or null/blank to look up the caller's own public IP
     * @throws LookupFailedException on network errors, rate limiting, or a provider-side failure
     */
    IpInfo lookup(String ip);
}