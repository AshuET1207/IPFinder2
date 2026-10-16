package com.app.ipfinder.exception;

public class InvalidIpException extends RuntimeException {

    public InvalidIpException(String message) {
        super(message);
    }
}