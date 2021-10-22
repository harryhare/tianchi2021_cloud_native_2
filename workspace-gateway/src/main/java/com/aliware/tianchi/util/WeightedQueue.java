package com.aliware.tianchi.util;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedQueue {
    static private class WorkRequest {
        final int index;
        final Double latency;

        WorkRequest(int index, long latency) {
            this.index = index;
            this.latency = latency + ThreadLocalRandom.current().nextDouble();
        }
    }

    static ConcurrentSkipListSet<WorkRequest> q;

    static {
        q = new ConcurrentSkipListSet<>(new Comparator<WorkRequest>() {
            @Override
            public int compare(WorkRequest o1, WorkRequest o2) {
                return o1.latency.compareTo(o2.latency);
            }
        });
        for (int i = 0; i < 100; i++) {
            q.add(new WorkRequest(0, 100));
            q.add(new WorkRequest(1, 100));
            q.add(new WorkRequest(2, 100));
        }
    }

    public static int get() {
        WorkRequest r = q.pollFirst();
        if (r != null) {
            return r.index;
        }
        return ThreadLocalRandom.current().nextInt(3);
    }

    public static void ok(int i, int duration) {//d 微秒 1e-6s
        if (duration < 3000) {//3ms
            q.pollLast();
            q.add(new WorkRequest(i, duration));
        }
        q.add(new WorkRequest(i, 10));
    }

    public static void err() {

    }
}
