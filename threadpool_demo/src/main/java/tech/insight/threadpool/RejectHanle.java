package tech.insight.threadpool;

/**
 *  
 */
public interface RejectHanle {
    void reject(Runnable command, MyThreadPool threadPool);
}
