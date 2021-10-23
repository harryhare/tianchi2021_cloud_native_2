package com.aliware.tianchi;

import com.aliware.tianchi.util.WeightedQueue;

public class entry {
    static public void main(String[] args){
        WeightedQueue.ok(3,3,false);
        WeightedQueue.ok(2,2,false);
        WeightedQueue.ok(1,1,false);
        WeightedQueue.ok(1,1,false);
        System.out.println(WeightedQueue.get());
        System.out.println(WeightedQueue.get());
        System.out.println(WeightedQueue.get());
    }
}
