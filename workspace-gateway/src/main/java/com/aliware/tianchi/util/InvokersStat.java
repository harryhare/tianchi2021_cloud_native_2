package com.aliware.tianchi.util;

import org.apache.dubbo.rpc.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class InvokersStat {
    static public class InvokerStat {
        private int[] response_time = new int[2000];
        AtomicInteger response_index = new AtomicInteger(0);
        AtomicInteger estimate = new AtomicInteger(500);//估计容量，瞬时
        AtomicInteger concurrent = new AtomicInteger(0); // 瞬时
        AtomicInteger err_all = new AtomicInteger(0);// 累加，阶段清零
        AtomicInteger err_timeout = new AtomicInteger(0);// 累加, 阶段清零
        AtomicInteger err_offline = new AtomicInteger(0);// 累加，succ清零
        public double rtt_period = 0;// 阶段平均

        int weight() {
            int o = err_offline.get();
            if (o > 0) {
                return 0;
            }
            return estimate.get() - concurrent.get();
        }

        void invoke() {
            concurrent.getAndIncrement();
        }

        void ok(long d) {
            concurrent.getAndDecrement();
            err_offline.set(0);
            //estimate.getAndAdd(2);
        }

        void err(ErrorType t) {
            concurrent.getAndDecrement();
            err_all.getAndIncrement();
            if (t == ErrorType.TIMEOUT) {
                err_timeout.getAndIncrement();
            } else if (t == ErrorType.OFFLINE) {
                err_offline.getAndIncrement();
            }
            //estimate.getAndAdd(-2);
        }

        void new_period() {
            err_all.set(0);
            err_timeout.set(0);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(InvokersStat.class);
    private static Timer timer = new Timer();
    private static int period = 0;
    private static long start = System.nanoTime();
    private InvokerStat[] a = new InvokerStat[3];
    private Map<String, InvokerStat> m = new HashMap<>();
    private static InvokersStat instance = null;

    private <T> void init_internal(List<Invoker<T>> invokers) {
        LOGGER.info("init_internal");
        for (int i = 0; i < 3; i++) {
            a[i] = new InvokerStat();
        }
        for (int i = 0; i < 3; i++) {
            //dubbo://172.21.208.1:20880/com.aliware.tianchi.HashInterface?anyhost=true&application=service-provider&category=providers&deprecated=false&dispatcher=game&dubbo=2.0.2&dynamic=true&generic=false&heartbeat=0&interface=com.aliware.tianchi.HashInterface&metadata-service-port=20880&methods=hash&path=com.aliware.tianchi.HashInterface&pid=28840&protocol=dubbo&release=3.0.1&side=provider&threads=500&timeout=5000&timestamp=1634376927976
            LOGGER.info(invokers.get(i).getUrl().toString());
            LOGGER.info(invokers.get(i).getUrl().getHost());
            String key = get_invoker_key(invokers.get(i));
            m.put(key, a[i]);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                period();
            }
        }, 0, 1000);
    }

    static public InvokersStat getInstance() {
        return instance;
    }

    static synchronized public <T> void init(List<Invoker<T>> invokers) {
        LOGGER.info("init");
        if (instance != null) {
            return;
        }
        instance = new InvokersStat();
        instance.init_internal(invokers);
    }

    // 按照估计的容量的剩余比例分配
    public int getByWeight() {
        int[] p = new int[3];
        for (int i = 0; i < 3; i++) {
            p[i] = a[i].weight();
            if (p[i] < 10) {
                p[i] = 10;
            }
        }

        int[] s = new int[3];
        s[0] = p[0];
        s[1] = p[1] + s[0];
        s[2] = p[2] + s[1];
        int r = ThreadLocalRandom.current().nextInt(s[2]);
        for (int i = 0; i < 2; i++) {
            if (r > s[i]) {
                return i;
            }
        }
        return 0;
    }

    public void period() {
        period++;
        print(period);
        for (int i = 0; i < 3; i++) {
            a[i].new_period();
        }
    }

    public void print(int index) {
        LOGGER.info("=>,{},{},{},{},{},{},{},{},{},{}", index,
                a[0].concurrent.get(), a[1].concurrent.get(), a[2].concurrent.get(),
                a[0].err_timeout.get(), a[1].err_timeout.get(), a[2].err_timeout.get(),
                a[0].err_offline.get(), a[1].err_offline.get(), a[2].err_offline.get()
        );
    }

    public void invoke(int id) {
        a[id].invoke();
    }

    public void ok(int id) {
        a[id].ok(0);
    }

    public void err(int id, ErrorType t) {
        a[id].err(t);
    }

    private static String get_invoker_key(Invoker<?> invoker) {
        return invoker.getUrl().getHost();
    }

    public void invoke(Invoker<?> invoker) {
        m.get(get_invoker_key(invoker)).invoke();
    }

    public void ok(Invoker<?> invoker) {
        m.get(get_invoker_key(invoker)).ok(0);
    }

    public enum ErrorType {
        OTHER,
        TIMEOUT,
        OFFLINE,
    }

    public void err(Invoker<?> invoker, ErrorType t) {
        m.get(get_invoker_key(invoker)).err(t);
    }
}
