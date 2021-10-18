package com.aliware.tianchi.util;

public class MyLog {

    public static void printf(String format, Object... args) {
        if (MyConfig.DEBUG) {
            System.out.printf(format, args);
        }
    }

    public static void println() {
        if (MyConfig.DEBUG) {
            System.out.println();
        }
    }

    public static void println(String x) {
        if (MyConfig.DEBUG) {
            System.out.println(x);
        }
    }

    public static void println(long x) {
        if (MyConfig.DEBUG) {
            System.out.println(x);
        }
    }
}
