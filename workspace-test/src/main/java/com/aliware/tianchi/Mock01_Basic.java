package com.aliware.tianchi;

import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;

public class Mock01_Basic {
    static double global_time = 0;

    static class MockConsumer {
        final int MAX_CONNECTION = 600;
        final int test_time = 60_000; //60s
        MockProvider[] providers = new MockProvider[3];

        public void init() {
            providers[0] = new MockProvider(1, 0.1);
            providers[1] = new MockProvider(1, 0.2);
            providers[2] = new MockProvider(1, 0.3);
        }

        public int score() {
            int connection = 0;
            int request_counter = 0;
            int timeout_counter = 0;
            int success_counter = 0;
            while (global_time < test_time) {
                // 回收connection
                if (connection >= MAX_CONNECTION) {
                    int min_i = -1;
                    double min_t = Double.MAX_VALUE;
                    for (int i = 0; i < 3; i++) {
                        double t = providers[i].peek();
                        if (t < min_t) {
                            min_t = t;
                            min_i = i;
                        }
                    }
                    providers[min_i].poll();
                    assert min_i != -1;
                    global_time =min_t;
                    connection--;
                    success_counter++;
                }

                // 调用
                int r = ThreadLocalRandom.current().nextInt(3);
                providers[r].invoke();
                request_counter++;
                connection++;
            }
            return request_counter;
        }
    }

    static class MockProvider {
        PriorityQueue<Double> q = new PriorityQueue<>();
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
            Double r = q.peek();
            if (r == null) {
                return Double.MAX_VALUE;
            }
            return r;
        }

        public void poll() {
            q.poll();
        }

        public void invoke() {
            double t = global_time + get_rtt();
            q.add(t);
        }
    }

    public static void print() {
        System.out.println("for test");
    }


    public static void main(String[] args) {
        System.out.println("Mocking...");
        MockConsumer consumer = new MockConsumer();
        consumer.init();
        int score = consumer.score();
        System.out.printf("score %d\n", score);
    }
}
