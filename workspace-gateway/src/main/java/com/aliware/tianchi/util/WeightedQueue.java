package com.aliware.tianchi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeightedQueue.class);

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
        for (int i = 0; i < 200; i++) {
            q.add(new WorkRequest(0, 1000));
            q.add(new WorkRequest(1, 1000));
            q.add(new WorkRequest(2, 1000));
        }
    }


    public static int get() {
        WorkRequest r = q.pollFirst();
        if (r != null) {
            MyLog.printf("invoke:queue out %d\n", r.index);
            return r.index;
        }
        return ThreadLocalRandom.current().nextInt(3);
    }

    public static void ok(int i, int duration, boolean good) {//d 微秒 1e-6s
        if (good) {//3ms
            q.pollLast();
            q.add(new WorkRequest(i, duration));
            MyLog.printf("ok:queue in %d\n", i);
        }
        q.add(new WorkRequest(i, duration));
        MyLog.printf("ok:queue in %d\n", i);
    }

    public static void err(int i) {
        i = (i + ThreadLocalRandom.current().nextInt(2) + 1) % 3;
        q.add(new WorkRequest(i, 1000));
//        WorkRequest r = q.pollFirst();
//        q.add(r);
//        q.add(new WorkRequest(r.index, (int) Math.floor(r.latency)));
//        MyLog.printf("err:queue in %d\n", i);
    }
}
