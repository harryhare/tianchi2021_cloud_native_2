package com.aliware.tianchi;

import com.aliware.tianchi.util.WeightedQueue;
import org.junit.Test;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.PriorityBlockingQueue;

public class TestQueue {
    @Test
    public void test0() {
        ConcurrentLinkedQueue<Integer> q = new ConcurrentLinkedQueue<>();
        q.add(1);
        q.add(2);
        q.add(3);
        q.add(1);
        System.out.println(q.poll());
        System.out.println(q.poll());
        System.out.println(q.poll());
        System.out.println(q.poll());
        System.out.println(q.poll());
    }

    @Test
    public void test() {
        ConcurrentSkipListSet<Integer> q = new ConcurrentSkipListSet<>();
        q.add(1);
        q.add(2);
        q.add(3);
        q.add(1);
        System.out.println(q.pollFirst());
        System.out.println(q.pollFirst());
        System.out.println(q.pollFirst());
        System.out.println(q.pollFirst());
    }

    @Test
    public void test2() {
        PriorityQueue<Integer> q = new PriorityQueue<>();
        q.add(1);
        q.add(1);
        q.add(2);
        q.add(3);
        System.out.println(q.poll());
        System.out.println(q.poll());
        System.out.println(q.poll());
        System.out.println(q.poll());
    }

    @Test
    public void test3() {
        PriorityBlockingQueue<Integer> q = new PriorityBlockingQueue<>();
        q.add(1);
        q.add(1);
        q.add(2);
        q.add(3);
        System.out.println(q.poll());
        System.out.println(q.poll());
        System.out.println(q.poll());
        System.out.println(q.poll());
        System.out.println(q.poll());
    }

    @Test
    public void test4() {
        PriorityQueue<Request> q = new PriorityQueue<>(Request.comparator);
        q.add(new Request(1, 6, 0));
        q.add(new Request(2, 2, 0));
        q.add(new Request(3, 3, 0));
        q.add(new Request(4, 4, 0));
        System.out.println(q.poll());
        System.out.println(q.poll());
        System.out.println(q.poll());
        System.out.println(q.poll());
        System.out.println(q.poll());
    }

    @Test
    public void test5() {
        ConcurrentSkipListSet<WeightedQueue.WorkRequest> q = new ConcurrentSkipListSet<>(new Comparator<WeightedQueue.WorkRequest>() {
            @Override
            public int compare(WeightedQueue.WorkRequest o1, WeightedQueue.WorkRequest o2) {
                return o1.latency.compareTo(o2.latency);
            }
        });
        for (int i = 0; i < 10; i++) {
            q.add(new WeightedQueue.WorkRequest(0, 0));//100ms
            q.add(new WeightedQueue.WorkRequest(1, 0));//100ms
            q.add(new WeightedQueue.WorkRequest(2, 0));//100ms
        }
        for (int i = 0; i < 10; i++) {
            q.add(new WeightedQueue.WorkRequest(0, 1));//100ms
            q.add(new WeightedQueue.WorkRequest(1, 1));//100ms
            q.add(new WeightedQueue.WorkRequest(2, 1));//100ms
        }
        for (int i = 0; i < 10; i++) {
            WeightedQueue.WorkRequest r = q.pollFirst();
            System.out.println(r);
        }
        for (int i = 0; i < 10; i++) {
            WeightedQueue.WorkRequest r = q.pollLast();
            System.out.println(r);
        }
    }
}
