package com.aliware.tianchi;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

public class TestSimulate {

    @Test
    public void test_random_rtt() {
        final int n = 1000;
        double total_time = 0;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i = 0; i < n; i++) {
            total_time += r.nextDouble();
        }
        double avg_rtt = total_time / n;
        double qps = n / total_time;
        System.out.printf("rtt %.3f, qps %.3f\n", avg_rtt, qps);
    }

    /*
        并发 221 就打满了
     */
    @Test
    public void test_rtt_concurrent() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        double a = r.nextDouble(0.6, 1.4);
        double b = r.nextDouble();
        double index = 1.02;
        int concurrent_max = 100;
        for (int i = 0; i < 500; i++) {
            double rtt = Math.pow(index, i) * Math.pow(i, 0.5) * 0.5;
            System.out.printf("current %d, rtt %.3f\n", i, rtt);
            if(rtt>5000){
                break;
            }
        }
    }
}
