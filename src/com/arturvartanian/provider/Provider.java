package com.arturvartanian.provider;

import java.util.Objects;

public class Provider<T> {
    private final T name;
    private boolean excluded;
    private boolean active;
    private boolean healthy; // for imitation

    public Provider(T name) {
        this.name = name;
        healthy = true;
        excluded = false;
        active = true;
    }

    public T get() {
        return name;
    }

    public int getAvailableRequests() {
        return 2;
    }

    public synchronized boolean check() {
        return healthy;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public synchronized void setExcluded(boolean excluded) {
        active = !excluded && healthy;
        this.excluded = excluded;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provider<?> provider = (Provider<?>) o;
        return excluded == provider.excluded && active == provider.active && healthy == provider.healthy && Objects.equals(name, provider.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, excluded, active, healthy);
    }
}
