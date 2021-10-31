package com.aliware.tianchi;


import com.aliware.tianchi.util.MyLog;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestBasic {
    @Test
    public void Test0(){
        MyLog.println("test");
        Mock01_Basic.print();
        System.out.println(Math.exp(1));
        System.out.println(Math.log(Math.exp(1)));
        System.out.printf("%s\n",null);

        Map<Integer,Integer> m=new HashMap<Integer,Integer>();
        m.put(1,1);
        System.out.println(m.get(1));
        System.out.println(m.get(2));
    }
}
