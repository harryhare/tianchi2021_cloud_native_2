package com.aliware.tianchi;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class TestSet {

    @Test
    public void test(){
        Set<Integer> s=new HashSet<>();
        s.add(0);
        s.add(0);
        s.add(1);
        s.add(2);
        System.out.println(s.remove(0));
        System.out.println(s.remove(0));
    }
}
