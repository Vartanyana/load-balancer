package com.arturvartanian.loadBalancer.healthcheck;

import com.arturvartanian.provider.Provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleHealthChecker implements HealthChecker<String> {
    private static final int SUBSEQUENT_HEALTH_COUNTER_TO_ACTIVATE = 2;

    private final Map<String, AtomicInteger> healthChecksPerProvider;

    public SimpleHealthChecker() {
        this.healthChecksPerProvider = new HashMap<>();
    }

    @Override
    public void healthCheck(List<Provider<String>> providers) {
        providers.parallelStream()
                .forEach(provider -> {
                    if (!healthChecksPerProvider.containsKey(provider.get())) {
                        healthChecksPerProvider.put(provider.get(), new AtomicInteger());
                    }

                    AtomicInteger healthRequests = healthChecksPerProvider.get(provider.get());

                    if (provider.isExcluded()) {
                        healthRequests.set(0);
                        return;
                    }

                    if (provider.isHealthy()) {
                        healthRequests.incrementAndGet();
                    } else {
                        provider.setActive(false);
                        healthRequests.set(0);
                    }

                    if (healthRequests.get() == SUBSEQUENT_HEALTH_COUNTER_TO_ACTIVATE) {
                        provider.setActive(true);
                        healthRequests.set(0);
                    }
                });
    }
}
