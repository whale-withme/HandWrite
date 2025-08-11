package tech.insight.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.management.RuntimeErrorException;

/**
 *  
 */
public class MyThreadPool {
    private final int coresize;
    private final int maxsize;
    private final int time;
    private final TimeUnit timeUnit;
    public final BlockingQueue<Runnable> blockingQueue;
    public final List<Thread> coreList = new ArrayList<>(1024);
    public final List<Thread> supportList = new ArrayList<>(1024);
    private final RejectHanle rejectHanle;

    public MyThreadPool(int coresize, int maxsize, int time, TimeUnit timeUnit, RejectHanle rejectHanle,
    BlockingQueue<Runnable> blockingQueue){
        this.coresize = coresize;
        this.maxsize = maxsize;
        this.blockingQueue = blockingQueue;
        this.time = time;
        this.timeUnit = timeUnit;
        this.rejectHanle = rejectHanle;
    }


    void excute(Runnable command){
        if(coreList.size() < coresize){
            Thread thread = new coreThread(command);
            coreList.add(thread);
            thread.start();
            return ;
        }

        if(blockingQueue.offer(command)){
            return ;
        }

        if(coreList.size() + supportList.size() < maxsize){
            Thread thread = new supportThread(command);
            supportList.add(thread);
            thread.start();
            return ;
        }

        if(!blockingQueue.offer(command)){
            rejectHanle.reject(command, this);
        }
    }


    class coreThread extends Thread{
        private final Runnable task;

        public coreThread(Runnable task){
            this.task = task;
        }

        @Override
        public void run() {
           task.run();

           while(true){
                try{
                    Runnable coreTask = blockingQueue.take();
                    coreTask.run();
                }catch(InterruptedException exception){
                    throw new RuntimeException(exception);
                }
           }
        }
        
    }

    class supportThread extends Thread{
        private final Runnable task;

        public supportThread(Runnable task){
            this.task = task;
        }

        @Override
        public void run() {
           task.run();

           while(true){
                try{
                    Runnable task = blockingQueue.poll(time, timeUnit);
                    if(task == null){
                        break;
                        // throw new RuntimeException("无任务被辅助队列执行");
                        // reject handle
                    }
                    task.run();
                }catch(InterruptedException exception){
                    throw new RuntimeException(exception);
                }
           }

           System.out.println("辅助线程结束了 " + Thread.currentThread().getName());
           supportList.remove(Thread.currentThread()); // 回收辅助线程
        }
        
    }
}
