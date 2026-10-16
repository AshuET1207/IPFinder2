package com.app.ipfinder.exception;

/** Any failure to obtain data from the geo-IP provider. The message is safe to show to the user. */
public class LookupFailedException extends RuntimeException {

    public LookupFailedException(String message) {
        super(message);
    }

    public LookupFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}