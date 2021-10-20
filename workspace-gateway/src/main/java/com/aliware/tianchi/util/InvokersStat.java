package com.aliware.tianchi.util;

import org.apache.dubbo.rpc.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class InvokersStat {
    static public class InvokerStat {
        AtomicInteger estimate = new AtomicInteger(500);//估计容量，瞬时
        AtomicInteger concurrent = new AtomicInteger(0); // 瞬时
        AtomicInteger err_total = new AtomicInteger(0); // 累加
        AtomicInteger err_period = new AtomicInteger(0); //阶段累加
        public double rtt_period = 0;// 阶段平均

        int remain() {
            return estimate.get() - concurrent.get();
        }

        void invoke() {
            concurrent.getAndIncrement();
        }

        void ok() {
            concurrent.getAndDecrement();
            //estimate.getAndAdd(2);
        }

        void err() {
            concurrent.getAndDecrement();
            err_total.getAndIncrement();
            err_period.getAndIncrement();
            //estimate.getAndAdd(-2);
        }

        public void new_period() {
            err_period.set(0);
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
        System.out.println("init_internal");
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
        System.out.println("init");
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
            p[i] = a[i].remain();
            if (p[i] < 10) {
                p[i] = 10;
            }
        }

        int[] s = new int[3];
        s[0] = p[0];
        s[1] = p[0] + s[1];
        s[2] = p[1] + s[2];
        int r = ThreadLocalRandom.current().nextInt(s[2]);
        for (int i = 0; i < 2; i++) {
            if (r > s[i]) {
                return i;
            }
        }
        return 0;
    }

    public void period() {
        for (int i = 0; i < 3; i++) {
            a[i].new_period();
        }
        period++;
        print(period);
    }

    public void print(int index) {
        System.out.printf("data,%d,%d,%d,%d\n", index, a[0].concurrent.get(), a[1].concurrent.get(), a[2].concurrent.get());
    }

    public void invoke(int id) {
        a[id].invoke();
    }

    public void ok(int id) {
        a[id].ok();
    }

    public void err(int id) {
        a[id].err();
    }

    private static String get_invoker_key(Invoker<?> invoker) {
        return invoker.getUrl().getHost();
    }

    public void invoke(Invoker<?> invoker) {
        m.get(get_invoker_key(invoker)).invoke();
    }

    public void ok(Invoker<?> invoker) {
        m.get(get_invoker_key(invoker)).ok();
    }


    public void err(Invoker<?> invoker) {
        m.get(get_invoker_key(invoker)).err();
    }
}
