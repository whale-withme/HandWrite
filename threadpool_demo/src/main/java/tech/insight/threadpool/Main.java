package tech.insight.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        RejectHanle discardReject = new DiscardReject();
        MyThreadPool threadPool = new MyThreadPool(2, 4, 1, TimeUnit.SECONDS,
                discardReject, new ArrayBlockingQueue<>(2));
        
        for(int i = 0; i < 10; i++){
            threadPool.excute(() -> {
                try {
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + " 正在执行");
                } catch (Exception e) {
                    throw new RuntimeException();
                }
            });
        }

        System.out.println("主线程直接执行");
    }
}