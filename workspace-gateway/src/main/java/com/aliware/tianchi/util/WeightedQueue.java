package com.aliware.tianchi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeightedQueue.class);

    static public class WorkRequest {
        public final int index;
        public final Double latency;

        public WorkRequest(int index, long latency) {
            this.index = index;
            this.latency = latency + ThreadLocalRandom.current().nextDouble();
        }

        @Override
        public String toString() {
            return String.format("%d:%.2f", index, latency);
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
            q.add(new WorkRequest(0, 0));//100ms
            q.add(new WorkRequest(1, 0));//100ms
            q.add(new WorkRequest(2, 0));//100ms
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
            WorkRequest r = q.pollLast();
            if (r != null && r.latency > duration) {
                q.add(new WorkRequest(i, duration));
            }
            MyLog.printf("ok:queue in %d\n", i);
        }
        q.add(new WorkRequest(i, duration));
        MyLog.printf("ok:queue in %d\n", i + 1000);
    }

    public static void err(int i, InvokersStat.ErrorType t, int subside) {
        q.add(new WorkRequest(subside, 0));
        MyLog.printf("err:queue in %d, subside %d\n", i, subside);
    }
}
