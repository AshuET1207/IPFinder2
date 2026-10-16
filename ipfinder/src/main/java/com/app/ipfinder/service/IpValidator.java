package com.app.ipfinder.service;

import com.app.ipfinder.exception.InvalidIpException;
import org.springframework.stereotype.Component;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

@Component
public class IpValidator {

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)"
                    + "(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}$"
    );

    public void validate(String ip) {
        if (ip == null || ip.isBlank()) {
            throw new InvalidIpException("IP address cannot be empty.");
        }

        String value = ip.trim();

        if (isValidIpv4(value) || isValidIpv6(value)) {
            return;
        }

        throw new InvalidIpException(
                "Please enter a valid IPv4 or IPv6 address."
        );
    }

    private boolean isValidIpv4(String ip) {
        return IPV4_PATTERN.matcher(ip).matches();
    }

    private boolean isValidIpv6(String ip) {
        if (!ip.contains(":")) {
            return false;
        }

        try {
            InetAddress address = InetAddress.getByName(ip);
            return address instanceof Inet6Address
                    && address.getHostAddress().equalsIgnoreCase(ip);
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
