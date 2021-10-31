package com.aliware.tianchi;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Mock02_Queue {
    static double global_time = 0;


    static class MockConsumer {
        final int MAX_CONNECTION = 600;
        final int test_time = 60_000; //60s
        MockProvider[] providers = new MockProvider[3];
        PriorityQueue<Request> timeout_queue = new PriorityQueue<>(Request.comparator);
        Set<Integer> wait = new HashSet<>();// provider 返回不在队列中则超时，timeout返回不在队列中则以完成请求

        public void init() {
            providers[0] = new MockProvider(1, 0.1);
            providers[1] = new MockProvider(1, 0.2);
            providers[2] = new MockProvider(1, 0.3);
        }

        public int score() {
            int connection = 0;
            int id = 0;
            int timeout_counter = 0;
            int success_counter = 0;
            while (global_time <= test_time) {
                // 回收connection
                if (connection >= MAX_CONNECTION) {
                    int min_i = -1;
                    double min_t = Double.MAX_VALUE;
                    // 处理超时队列
                    double t = Util.peekInSet(timeout_queue, wait);
                    if (t < min_t) {
                        min_i = 3;
                        min_t = t;
                    }

                    // 处理provider
                    for (int i = 0; i < 3; i++) {
                        t = Util.peekInSet(providers[i].q, wait);
                        if (t < min_t) {
                            min_t = t;
                            min_i = i;
                        }
                    }
                    assert min_i != -1;
                    Request r = null;
                    if (min_i < 3) {
                        assert min_i >= 0;
                        r = providers[min_i].poll();
                        //System.out.printf("%d, suc, %.0f\n", r.id, r.time_out_queue);
                    } else {
                        assert min_i == 3;
                        r = timeout_queue.poll();
                        System.out.printf("%d, err, %.0f\n", r.id, r.time_out_queue);
                    }
                    assert r != null;
                    wait.remove(r.id);
                    global_time = min_t;
                    connection--;
                    success_counter++;
                }

                // 调用
                int r = ThreadLocalRandom.current().nextInt(3);
                providers[r].invoke(id);
                wait.add(id);
                timeout_queue.add(new Request(id, global_time + 10, 10));
                id++;
                connection++;
            }
            System.out.printf("total: %d\n", id);
            System.out.printf("suc:   %d\n", success_counter);
            System.out.printf("err:   %d\n", id - success_counter);
            return success_counter;
        }
    }

    static class MockProvider {
        PriorityQueue<Request> q = new PriorityQueue<>(Request.comparator);
        public double index;
        public double err;

        public MockProvider(double index, double err) {
            this.index = index;
            this.err = err;
        }

        public double get_rtt() {
            //return ThreadLocalRandom.current().nextDouble(5);//ms
            return 1;
        }

        public double peek() {
            Request r = q.peek();
            if (r == null) {
                return Double.MAX_VALUE;
            }
            return r.time_out_queue;
        }

        public Request poll() {
            return q.poll();
        }

        public void invoke(int id) {
            double t = get_rtt();
            q.add(new Request(id, global_time + t, (int) (t * 1000)));
        }
    }



    public static void main(String[] args) {
        System.out.println("Mocking...");
        MockConsumer consumer = new MockConsumer();
        consumer.init();
        int score = consumer.score();
        System.out.printf("score %d\n", score);
    }
}
