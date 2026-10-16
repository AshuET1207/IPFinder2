package com.app.ipfinder.service;

import com.app.ipfinder.exception.InvalidIpException;
import org.springframework.stereotype.Component;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

@Component
public class LookupQueryValidator {

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)"
                    + "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$"
    );

    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
            "^(?=.{1,253}$)"
                    + "(?:[a-zA-Z0-9]"
                    + "(?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?"
                    + "\\.)*"
                    + "[a-zA-Z0-9]"
                    + "(?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?$"
    );

    public void validate(String query) {
        if (query == null || query.isBlank()) {
            throw new InvalidIpException("IP address or domain name cannot be empty.");
        }

        String value = query.trim();

        if (isValidIpv4(value)
                || isValidIpv6(value)
                || isValidDomain(value)) {
            return;
        }

        throw new InvalidIpException(
                "Enter a valid IPv4, IPv6 address, or domain name."
        );
    }

    private boolean isValidIpv4(String value) {
        return IPV4_PATTERN.matcher(value).matches();
    }

    private boolean isValidIpv6(String value) {
        if (!value.contains(":")) {
            return false;
        }

        try {
            InetAddress address = InetAddress.getByName(value);

            return address instanceof Inet6Address
                    && address.getHostAddress().equalsIgnoreCase(value);
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private boolean isValidDomain(String value) {
        return DOMAIN_PATTERN.matcher(value).matches()
                && value.contains(".");
    }
}