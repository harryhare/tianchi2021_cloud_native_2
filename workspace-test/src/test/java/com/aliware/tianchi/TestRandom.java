package com.aliware.tianchi;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

public class TestRandom {
    @Test
    public void test(){
        // 0-1 之间
        System.out.println(ThreadLocalRandom.current().nextDouble());
        System.out.println(ThreadLocalRandom.current().nextDouble());
        System.out.println(ThreadLocalRandom.current().nextDouble());
        System.out.println(ThreadLocalRandom.current().nextInt(0));
    }
}
