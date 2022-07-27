package com.arturvartanian.loadBalancer;

import com.arturvartanian.exception.LoadBalancingException;
import com.arturvartanian.provider.Provider;

import java.util.List;

public interface LoadBalancer<T> {

    void register(final List<Provider<T>> providers);
    T get() throws LoadBalancingException;
    void exclude(final String name);
    void include(final String name);

}
