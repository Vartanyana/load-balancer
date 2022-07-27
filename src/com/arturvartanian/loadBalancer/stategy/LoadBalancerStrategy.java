package com.arturvartanian.loadBalancer.stategy;

public interface LoadBalancerStrategy {
    int getNextProviderIndex(final int size);
}
