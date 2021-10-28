package com.aliware.tianchi;

import java.util.concurrent.ThreadLocalRandom;

public class Entry {
    class MockConsumer {
        final int connection = 600;

        public int score() {

            return 0;
        }
    }

    class MockProvider {

    }

    public static void print() {
        System.out.println("for test");
    }


    public static void main(String[] args) {
        System.out.println("test");
        // 0-1 之间
        System.out.println(ThreadLocalRandom.current().nextDouble());
        System.out.println(ThreadLocalRandom.current().nextDouble());
        System.out.println(ThreadLocalRandom.current().nextDouble());
        System.out.println(ThreadLocalRandom.current().nextInt(0));

    }
}
