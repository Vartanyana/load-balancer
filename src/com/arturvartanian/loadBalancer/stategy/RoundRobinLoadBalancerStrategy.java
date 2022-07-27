package com.arturvartanian.loadBalancer.stategy;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancerStrategy implements LoadBalancerStrategy {

    private final AtomicInteger nextCounter;

    public RoundRobinLoadBalancerStrategy() {
        nextCounter = new AtomicInteger(-1);
    }

    @Override
    public int getNextProviderIndex(final int size) {
        int current;
        int next;
        do {
            current = nextCounter.get();
            next = current + 1 < size ? current + 1 : 0;
        } while (!nextCounter.compareAndSet(current, next));
        return next;
    }
}
