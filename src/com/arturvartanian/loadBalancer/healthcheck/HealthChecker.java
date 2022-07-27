package com.arturvartanian.loadBalancer.healthcheck;

import com.arturvartanian.provider.Provider;

import java.util.List;

public interface HealthChecker<T> {
    void healthCheck(List<Provider<T>> providers);
}
