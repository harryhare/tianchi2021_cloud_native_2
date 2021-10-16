package com.aliware.tianchi.util;

public class Util {
    public static int get_opt_concurrent(double index){
        return (int)(1/(2*Math.log(index)));
    }
}
