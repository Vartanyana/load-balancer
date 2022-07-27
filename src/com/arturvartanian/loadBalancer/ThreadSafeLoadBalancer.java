package com.arturvartanian.loadBalancer;

import com.arturvartanian.exception.LoadBalancingException;
import com.arturvartanian.loadBalancer.healthcheck.HealthChecker;
import com.arturvartanian.provider.Provider;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class ThreadSafeLoadBalancer<T> implements LoadBalancer<T> {

    private final HealthChecker<T> healthChecker;

    private final ReadWriteLock readWriteLock;

    public ThreadSafeLoadBalancer(HealthChecker<T> healthChecker) {
        this.healthChecker = healthChecker;
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public void register(List<Provider<T>> providers) {
        readWriteLock.writeLock().lock();
        try {
            registerProvider(providers);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public T get() throws LoadBalancingException {
        readWriteLock.readLock().lock();
        try {
            return getProvider();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void exclude(String name) {
        readWriteLock.writeLock().lock();
        try {
            excludeProvider(name);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void include(String name) {
        readWriteLock.writeLock().lock();
        try {
            includeProvider(name);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    protected void healthCheck() {
        readWriteLock.writeLock().lock();
        healthChecker.healthCheck(getProvidersList());
        readWriteLock.writeLock().unlock();
    }

    protected abstract void registerProvider(final List<Provider<T>> providers);
    protected abstract T getProvider() throws LoadBalancingException;
    protected abstract void excludeProvider(final String name);
    protected abstract void includeProvider(final String name);
    protected abstract List<Provider<T>> getProvidersList();
}
