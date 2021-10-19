package com.aliware.tianchi.util;

public class MyConfig {
    public static boolean DEBUG = false;

    static {
        String value = System.getProperty("debug");
        if ("true".equals(value)) {
            System.out.println("debug=true");
            DEBUG = true;
        } else {
            System.out.println("debug=false");
        }
    }
}
