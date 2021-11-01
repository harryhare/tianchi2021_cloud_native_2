package com.aliware.tianchi;

import com.aliware.tianchi.util.InvokersStat;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// 代入提交代码中的 测试类
public class Mock10 {
    static double global_time = 0;


    static class MockConsumer {
        final int MAX_CONNECTION = 600;
        final int test_time = 60_000; //60s
        MockProvider[] providers = new MockProvider[3];
        PriorityQueue<Request> timeout_queue = new PriorityQueue<>(Request.comparator);
        Map<Integer, Integer> wait = new HashMap<>();// provider 返回不在队列中则超时，timeout返回不在队列中则以完成请求
        InvokersStat invoker = null;

        public void init() {
            InvokersStat.mock_init();
            invoker = InvokersStat.getInstance();
            providers[0] = new MockProvider(1, 0.1);
            providers[1] = new MockProvider(1, 0.2);
            providers[2] = new MockProvider(1, 0.3);
        }

        public int score() {
            int connection = 0;
            int id = 0;
            int timeout_counter = 0;
            int success_counter = 0;
            int last_mark_time = 0;
            while (global_time <= test_time) {
                // 调用invoker 的period ，并且输出log 记录进展
                if (((int) global_time / 1000 - last_mark_time) >= 1) {
                    last_mark_time = (int) global_time / 1000;
                    invoker.period();
                    //System.out.printf("processing 00:%02d\n",last_mark_time);
                }

                // 回收connection
                if (connection >= MAX_CONNECTION) {
                    int min_i = -1;
                    double min_t = Double.MAX_VALUE;
                    // 处理超时队列
                    double t = Util.peekInMap(timeout_queue, wait);
                    if (t < min_t) {
                        min_i = 3;
                        min_t = t;
                    }

                    // 处理provider
                    for (int i = 0; i < 3; i++) {
                        t = Util.peekInMap(providers[i].q, wait);
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
                        invoker.ok(min_i, r.duration);
                        success_counter++;
                    } else {
                        assert min_i == 3;
                        r = timeout_queue.poll();
                        //System.out.printf("%d, err, %.0f\n", r.id, r.time_out_queue);
                        invoker.err(wait.get(r.id), InvokersStat.ErrorType.TIMEOUT);
                        timeout_counter++;
                    }
                    assert r != null;
                    wait.remove(r.id);
                    global_time = min_t;
                    connection--;
                }

                // 调用
                //int r = 0;// 17_050_547/18_945_963
                //int r = ThreadLocalRandom.current().nextInt(3);// 10_281_296/12_853_557
                //int r = InvokersStat.getInstance().chooseByWeight(); //12_574_883/14_917_826
                int r = invoker.chooseByConcurrent(); // 13_713_870/15_942_934
                //int r = InvokersStat.getInstance().chooseByQueue(); // 13690701 // 15_922055
                providers[r].invoke(id);
                invoker.invoke(r);
                wait.put(id, r);
                timeout_queue.add(new Request(id, global_time + 10, 5000000));
                id++;
                connection++;
            }
            System.out.printf("total: %d\n", id);
            System.out.printf("suc:   %d\n", success_counter);
            System.out.printf("err:   %d\n", timeout_counter);
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
            double r = ThreadLocalRandom.current().nextDouble();
            if (r < this.err) {
                //System.out.println("throw away");
                return;
            }
            double rtt = get_rtt();
            Request request = new Request(id, global_time + rtt, (int) (rtt * 1000));
            q.add(request);
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
