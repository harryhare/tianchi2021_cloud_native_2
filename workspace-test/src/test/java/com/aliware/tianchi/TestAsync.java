package com.aliware.tianchi;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class TestAsync {
    private static CompletableFuture<String> getData(int s, String data) {
        return CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(s);
                    return data;
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    private static CompletableFuture<Void> getSleep(int s) {
        return CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                // Simulate a long-running Job
                try {
                    TimeUnit.SECONDS.sleep(s);
                    //Thread.sleep(1000*s);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                System.out.println("I'll run in a separate thread than the main thread.");
            }
        });
    }

    @Test
    public void first_no_arg() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> f1 = getSleep(1);
        CompletableFuture<Void> f2 = getSleep(10);
        CompletableFuture<Void> f3 = getSleep(100);
        CompletableFuture<Object> f = CompletableFuture.anyOf(f1, f2, f3);
        //CompletableFuture<Void> f = CompletableFuture.allOf(f1, f2, f3);
        f.get();
    }

    @Test
    public void first_with_arg() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f1 = getData(1, "AAA");
        CompletableFuture<String> f2 = getData(2, "BBB");
        CompletableFuture<String> f3 = getData(3, "BBB");
        CompletableFuture<Object> f = CompletableFuture.anyOf(f1, f2, f3);
        String r = (String) f.get();
        System.out.println(r);
    }

    @Test
    public void test() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                // Simulate a long-running Job
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                System.out.println("I'll run in a separate thread than the main thread.");
            }
        });
// Block and wait for the future to complete
        future.get();
    }

    @Test
    public void test10() {
        //CompletableFuture<Integer> x = CompletableFuture.completedFuture(0);
        CompletableFuture<Integer> x = CompletableFuture.supplyAsync(() -> {
            throw new CompletionException(new Exception());
        });;
        x.whenComplete((value,t)->{
            System.out.println(value);
            System.out.println(t);
        });
    }
}
