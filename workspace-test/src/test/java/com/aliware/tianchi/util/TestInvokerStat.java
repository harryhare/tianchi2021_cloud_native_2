package com.aliware.tianchi.util;

import org.junit.Test;

public class TestInvokerStat {
    @Test
    public void test(){
        double[] a={3,2,1};
        InvokersStat.format(a);
        assert a[0]==300;
        assert a[1]==200;
        assert a[2]==100;
    }
    @Test
    public void test2(){
        double[] a={1,1,1};
        InvokersStat.format(a);
        assert a[0]==200;
        assert a[1]==200;
        assert a[2]==200;
    }
}
