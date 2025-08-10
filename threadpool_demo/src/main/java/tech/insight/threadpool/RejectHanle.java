package tech.insight.threadpool;

/**
 *  
 */
public interface RejectHanle {
    // todo: not relaize
    void reject(Runnable command, MyThreadPool threadPool);
}
