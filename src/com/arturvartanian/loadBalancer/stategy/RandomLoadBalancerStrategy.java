package com.arturvartanian.loadBalancer.stategy;

import java.util.Random;

public class RandomLoadBalancerStrategy implements LoadBalancerStrategy {

    private static final Random random = new Random();

    @Override
    public int getNextProviderIndex(final int size) {
        return random.nextInt(size);
    }
}