package com.aliware.tianchi.util;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCounter {
    private static AtomicInteger[] counter = new AtomicInteger[3];

    public static int get() {
        int[] a = new int[3];
        for (int i = 0; i < 3; i++) {
            a[i] = counter[i].get();
        }
        int min_i = 0;
        for (int i = 1; i < 3; i++) {
            if (a[i] < a[min_i]) {
                min_i = i;
            }
        }
        if (a[min_i] > 50) {
            return -1;
        }
        return min_i;
    }

    public static void ok(int id) {
        counter[id].decrementAndGet();
    }

    public static void err(int id) {
        counter[id].decrementAndGet();
    }
}
