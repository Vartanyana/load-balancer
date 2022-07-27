package com.arturvartanian;

import com.arturvartanian.loadBalancer.SimpleLoadBalancer;
import com.arturvartanian.loadBalancer.healthcheck.SimpleHealthChecker;
import com.arturvartanian.loadBalancer.stategy.RoundRobinLoadBalancerStrategy;
import com.arturvartanian.provider.Provider;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        SimpleLoadBalancer l = new SimpleLoadBalancer(new RoundRobinLoadBalancerStrategy(), new SimpleHealthChecker(), 3);
        l.register(List.of(new Provider<>("1"), new Provider<>("2"), new Provider<>("3"),
                           new Provider<>("4"), new Provider<>("5"), new Provider<>("6")));



        Executors.newSingleThreadScheduledExecutor().execute(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(1);
                    System.out.println(l.get());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        Executors.newSingleThreadScheduledExecutor().execute(() -> {
            try {
                for (int i = 1; i <= 6; i++) {
                    Thread.sleep(100);
                    l.exclude(String.valueOf(i));
                    System.out.println("excluding ---------- " + i);
                }

                for (int i = 1; i <= 6; i++) {
                    Thread.sleep(100);
                    l.include(String.valueOf(i));
                    System.out.println("including ---------- " + i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
