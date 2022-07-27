package com.arturvartanian.loadbalancer;

import com.arturvartanian.exception.NoAvailableProviderException;
import com.arturvartanian.exception.NoRegisteredProvidersException;
import com.arturvartanian.loadBalancer.SimpleLoadBalancer;
import com.arturvartanian.loadBalancer.healthcheck.SimpleHealthChecker;
import com.arturvartanian.loadBalancer.stategy.RandomLoadBalancerStrategy;
import com.arturvartanian.loadBalancer.stategy.RoundRobinLoadBalancerStrategy;
import com.arturvartanian.provider.Provider;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleLoadBalancerTest {
    private static final String PROVIDER_TEST_NAME = "test";
    private static final String PROVIDER_TEST_ONE_NAME = "test1";
    private static final String PROVIDER_TEST_TWO_NAME = "test2";

    private static final List<String> PROVIDER_NAMES
            = List.of(PROVIDER_TEST_NAME, PROVIDER_TEST_ONE_NAME, PROVIDER_TEST_TWO_NAME);

    @Test
    public void testRegister() {
        SimpleLoadBalancer simpleLoadBalancer
                = new SimpleLoadBalancer(new RoundRobinLoadBalancerStrategy(), new SimpleHealthChecker(), 3);

        simpleLoadBalancer.register(buildProviderList());

        List<Provider<String>> actualProviders = simpleLoadBalancer.getProvidersList();

        assertEquals(3, actualProviders.size());

        Provider<String> actualTestProvider = actualProviders.get(0);
        assertEquals(PROVIDER_TEST_NAME, actualTestProvider.get());

    }

    @Test
    public void testRoundRobinGet() throws Exception {
        SimpleLoadBalancer simpleLoadBalancer
                = new SimpleLoadBalancer(new RoundRobinLoadBalancerStrategy(), new SimpleHealthChecker(), 3);
        simpleLoadBalancer.register(buildProviderList());

        assertEquals(PROVIDER_TEST_NAME, simpleLoadBalancer.get());
        assertEquals(PROVIDER_TEST_ONE_NAME, simpleLoadBalancer.get());
        assertEquals(PROVIDER_TEST_TWO_NAME, simpleLoadBalancer.get());
    }

    @Test
    public void testRandomGet() throws Exception {
        SimpleLoadBalancer simpleLoadBalancer
                = new SimpleLoadBalancer(new RandomLoadBalancerStrategy(), new SimpleHealthChecker(), 3);
        simpleLoadBalancer.register(buildProviderList());

        assertTrue(PROVIDER_NAMES.contains(simpleLoadBalancer.get()));
        assertTrue(PROVIDER_NAMES.contains(simpleLoadBalancer.get()));
        assertTrue(PROVIDER_NAMES.contains(simpleLoadBalancer.get()));
    }

    @Test(expected = NoRegisteredProvidersException.class)
    public void testNoRegisteredProvidersException() throws Exception {
        SimpleLoadBalancer simpleLoadBalancer
                = new SimpleLoadBalancer(new RandomLoadBalancerStrategy(), new SimpleHealthChecker(), 3);
        simpleLoadBalancer.get();
    }

    @Test(expected = NoAvailableProviderException.class)
    public void testNoAvailableProviderException() throws Exception {
        SimpleLoadBalancer simpleLoadBalancer
                = new SimpleLoadBalancer(new RandomLoadBalancerStrategy(), new SimpleHealthChecker(), 3);
        simpleLoadBalancer.register(buildProviderList());

        simpleLoadBalancer.getProvidersList().forEach(provider -> provider.setExcluded(true));

        simpleLoadBalancer.get();
    }

    private List<Provider<String>> buildProviderList() {
        return List.of(new Provider<>(PROVIDER_TEST_NAME), new Provider<>(PROVIDER_TEST_ONE_NAME),
                new Provider<>(PROVIDER_TEST_TWO_NAME));
    }
}
