package com.aliware.tianchi.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Util {
    private static CompletableFuture<Void> getSleep(int ms) {
        return CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(ms);
                    //Thread.sleep(ms);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }
    public static int get_opt_concurrent(double index){
        return (int)(1/(2*Math.log(index)));
    }
}
