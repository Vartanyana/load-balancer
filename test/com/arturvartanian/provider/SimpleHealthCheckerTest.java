package com.arturvartanian.provider;

import com.arturvartanian.loadBalancer.healthcheck.SimpleHealthChecker;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleHealthCheckerTest {

    @Test
    public void testOneTimeHealthCheck() {
        SimpleHealthChecker simpleHealthChecker = new SimpleHealthChecker();
        Provider<String> provider = new Provider<>("Name");
        provider.setActive(false);
        simpleHealthChecker.healthCheck(List.of(provider));
        assertFalse(provider.isActive());
    }

    @Test
    public void testTwoTimesHealthCheck() {
        SimpleHealthChecker simpleHealthChecker = new SimpleHealthChecker();
        Provider<String> provider = new Provider<>("Name");
        provider.setActive(false);

        simpleHealthChecker.healthCheck(List.of(provider));
        simpleHealthChecker.healthCheck(List.of(provider));

        assertTrue(provider.isActive());
    }

    @Test
    public void testTwoTimesExcludedHealthCheck() {
        SimpleHealthChecker simpleHealthChecker = new SimpleHealthChecker();
        Provider<String> provider = new Provider<>("Name");
        provider.setActive(false);
        provider.setExcluded(true);

        simpleHealthChecker.healthCheck(List.of(provider));
        simpleHealthChecker.healthCheck(List.of(provider));

        assertFalse(provider.isActive());
    }

    @Test
    public void testTwoTimesNotHealthyHealthCheck() {
        SimpleHealthChecker simpleHealthChecker = new SimpleHealthChecker();
        Provider<String> provider = new Provider<>("Name");
        provider.setActive(false);
        provider.setHealthy(false);

        simpleHealthChecker.healthCheck(List.of(provider));
        simpleHealthChecker.healthCheck(List.of(provider));

        assertFalse(provider.isActive());
    }

    @Test
    public void testTwoTimesGettingHealthyHealthCheck() {
        SimpleHealthChecker simpleHealthChecker = new SimpleHealthChecker();
        Provider<String> provider = new Provider<>("Name");
        provider.setActive(false);
        provider.setHealthy(false);

        simpleHealthChecker.healthCheck(List.of(provider));
        simpleHealthChecker.healthCheck(List.of(provider));

        assertFalse(provider.isActive());

        provider.setHealthy(true);

        simpleHealthChecker.healthCheck(List.of(provider));
        simpleHealthChecker.healthCheck(List.of(provider));

        assertTrue(provider.isActive());
    }
}
