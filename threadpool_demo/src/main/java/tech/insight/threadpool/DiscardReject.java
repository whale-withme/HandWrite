package tech.insight.threadpool;

/**
 *  
 */
public class DiscardReject implements RejectHanle{

    @Override
    public void reject(Runnable command, MyThreadPool threadPool) {
        threadPool.blockingQueue.poll();
        System.out.println("扔掉了一个任务");
        threadPool.blockingQueue.offer(command);
    }

}
