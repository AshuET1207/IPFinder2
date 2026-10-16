package com.app.ipfinder.exception;


public class HistoryStoreException extends RuntimeException {

    public HistoryStoreException(String message) {
        super(message);
    }

    public HistoryStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}