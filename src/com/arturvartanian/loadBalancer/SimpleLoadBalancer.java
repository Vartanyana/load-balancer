package com.arturvartanian.loadBalancer;

import com.arturvartanian.exception.LoadBalancingException;
import com.arturvartanian.exception.NoAvailableProviderException;
import com.arturvartanian.exception.NoRegisteredProvidersException;
import com.arturvartanian.loadBalancer.healthcheck.HealthChecker;
import com.arturvartanian.loadBalancer.stategy.LoadBalancerStrategy;
import com.arturvartanian.provider.Provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SimpleLoadBalancer extends ThreadSafeLoadBalancer<String> {
    private static final int LOAD_BALANCER_MAX_SIZE = 10;

    private final int heartBeatInvocationPeriod;

    private final List<Provider<String>> providers;

    private final Map<String, Semaphore> availableRequestsByProvider;

    private final LoadBalancerStrategy loadBalancerStrategy;

    private final ScheduledExecutorService executorService;

    public SimpleLoadBalancer(final LoadBalancerStrategy loadBalancerStrategy, HealthChecker<String> healthChecker,
                              final int heartBeatInvocationPeriod) {
        super(healthChecker);
        this.loadBalancerStrategy = loadBalancerStrategy;
        this.heartBeatInvocationPeriod = heartBeatInvocationPeriod;

        availableRequestsByProvider = new HashMap<>();
        executorService = Executors.newSingleThreadScheduledExecutor();
        enableHealthCheck();
        providers = new ArrayList<>();
    }

    @Override
    protected void registerProvider(List<Provider<String>> providers) {
        if (providers.size() + this.providers.size() > LOAD_BALANCER_MAX_SIZE) {
            throw new IllegalArgumentException(
                    String.format("The providers list size exceeds the maximum size of %d", LOAD_BALANCER_MAX_SIZE));
        }
        this.providers.addAll(providers);
        this.providers.forEach(provider -> availableRequestsByProvider.put(provider.get(), new Semaphore(provider.getAvailableRequests())));
    }

    @Override
    protected String getProvider() throws LoadBalancingException {
        String name = null;
        try {

            if (this.providers.isEmpty()) {
                throw new NoRegisteredProvidersException("There's no registered providers");
            }

            Provider<String> provider = providers.get(loadBalancerStrategy.getNextProviderIndex(providers.size()));
            int providerCounter = 1;
            while (providerCounter++ < providers.size() && isProviderAvailable(provider)){
                provider = providers.get(loadBalancerStrategy.getNextProviderIndex(providers.size()));
            }

            if (provider == null || provider.isExcluded()) {
                throw new NoAvailableProviderException("No available providers");
            }

            name = provider.get();
            availableRequestsByProvider.get(provider.get()).acquire();
            return provider.get();
        } catch (InterruptedException e) {
            System.err.println("Interrupted exception occurred");
            return null;
        } finally {
            if (availableRequestsByProvider.containsKey(name)) {
                availableRequestsByProvider.get(name).release();
            }
        }
    }

    @Override
    protected void excludeProvider(String name) {
        setExcluded(name, true);
    }

    @Override
    protected void includeProvider(String name) {
        setExcluded(name, false);
    }

    @Override
    public List<Provider<String>> getProvidersList() {
        return providers;
    }

    private boolean isProviderAvailable(final Provider<String> provider) {
        return provider == null || provider.isExcluded()
                || !provider.isActive() || availableRequestsByProvider.get(provider.get()).hasQueuedThreads();
    }

    private void enableHealthCheck() {
        executorService.scheduleAtFixedRate(this::healthCheck, 0, heartBeatInvocationPeriod, TimeUnit.SECONDS);
    }

    private void setExcluded(final String name, final boolean isExcluded) {
        providers.stream().filter(provider -> provider.get().equals(name))
                .findAny()
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format("Provider with name %s is not registered.", name)))
                .setExcluded(isExcluded);
    }
}
