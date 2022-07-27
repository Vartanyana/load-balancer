package com.arturvartanian.exception;

public class NoAvailableProviderException extends LoadBalancingException {
    public NoAvailableProviderException(String message) {
        super(message);
    }
}
