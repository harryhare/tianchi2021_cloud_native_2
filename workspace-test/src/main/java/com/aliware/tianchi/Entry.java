package com.aliware.tianchi;

import java.util.concurrent.ThreadLocalRandom;

public class Entry {
    public static void print(){
        System.out.println("for test");
    }
    public static void main(String[] args){
        System.out.println("test");
        // 0-1 之间
        System.out.println(ThreadLocalRandom.current().nextDouble());
        System.out.println(ThreadLocalRandom.current().nextDouble());
        System.out.println(ThreadLocalRandom.current().nextDouble());

    }
}
