package com.arturvartanian.exception;

public class NoRegisteredProvidersException extends LoadBalancingException {
    public NoRegisteredProvidersException(String message) {
        super(message);
    }
}
