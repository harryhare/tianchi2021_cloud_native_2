package com.aliware.tianchi;

import org.junit.Test;

import java.util.concurrent.ConcurrentSkipListSet;

public class TestQueue {
    @Test
    public void test(){
        ConcurrentSkipListSet<Integer> q=new ConcurrentSkipListSet<>();
        q.add(1);
        q.add(2);
        q.add(3);
        q.add(1);
        System.out.println(q.pollFirst());
        System.out.println(q.pollFirst());
        System.out.println(q.pollFirst());
        System.out.println(q.pollFirst());
    }
}
