package com.aliware.tianchi;

import org.junit.Test;

import java.util.PriorityQueue;
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
            if (rtt > 5000) {
                break;
            }
        }
    }

    private double get_rtt(double index, int c) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        double a = r.nextDouble(0.6, 1.4);
        double b = r.nextDouble();
        double rtt = Math.pow(index, c) * Math.pow(c, 0.5) * 0.5;
        if (rtt > 5000) {
            rtt = 5000;
        }
        return rtt;
    }

    @Test
    public void test1() {
        PriorityQueue<Double>[] q = new PriorityQueue[3];
        for (int i = 0; i < 3; i++) {
            q[i] = new PriorityQueue<>();
        }
        double[] index = {1.045, 1.03, 1.015};
        int counter = 0;
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 200; i++) {
                q[j].add(get_rtt(index[j], i));
                counter++;
            }
            q[j].add(Double.MAX_VALUE);
        }
        double[] a = new double[3];
        while (true) {
            Double t = Double.MAX_VALUE;
            int drop = -1;
            for (int i = 0; i < 3; i++) {
                a[i] = q[i].peek();
                if (a[i] < t) {
                    t = a[i];
                    drop = i;
                }
            }
            assert drop >= 0 && drop <= 2;
            assert q[drop].poll().equals(t);
            t += get_rtt(index[drop], q[drop].size());

            //int x = ThreadLocalRandom.current().nextInt(3);
            int x=drop;
            q[x].add(t);
            assert q[drop].size() < 500;
            counter++;
            if (t > 1000 * 120) {
                break;
            }
        }
        System.out.printf("total request: %d\n", counter);
    }
}
//179697，x=drop
//27382,x=随机