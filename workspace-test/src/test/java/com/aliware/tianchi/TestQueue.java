package com.aliware.tianchi;

import org.junit.Test;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.PriorityBlockingQueue;

public class TestQueue {
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
}
