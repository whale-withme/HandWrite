package tech.insight.lock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 *  
 */
public class MyLock {

    AtomicInteger state = new AtomicInteger(0);
    Thread owner = null;
    AtomicReference<Node> head = new AtomicReference<>(new Node());
    AtomicReference<Node> tail = new AtomicReference<>(head.get());

    void lock(){
        if(state.get() == 0){
            if(state.compareAndSet(0, 1)){
                owner = Thread.currentThread();
                System.out.println(Thread.currentThread().getName() + "拿到了锁");
                return ;
            }
        }else if(owner == Thread.currentThread()){
            int reenTime = state.incrementAndGet();
            System.out.println(Thread.currentThread().getName() + "重入锁" + reenTime + "次");
            return ;
        }

        Node cur = new Node();
        cur.thread = Thread.currentThread();

        while(true){
            Node tailNode = tail.get();
            if(tail.compareAndSet(tailNode, cur)){
                cur.pre = tailNode;
                tailNode.next = cur;
                break;
            }
        }

        while(true){
            if(head.get() == cur.pre && state.compareAndSet(0, 1)){
                owner = Thread.currentThread();
                head.set(cur);
                cur.pre.next = null;
                cur.pre = null;
                return ;
            }
            LockSupport.park();
        }
    }

    void unlock(){
        if(this.owner != Thread.currentThread()){
            throw new IllegalStateException("当前线程并没有锁，不能解锁"+Thread.currentThread().getName() + "owner" + owner.getName());
        }

        int remain = state.get();
        if(remain > 1){
            state.set(remain - 1);
            return ;
        }

        if(remain <= 0){
            throw new IllegalStateException("重入错误");
        }

        Node next = head.get().next;
        owner = null;
        state.set(0);
        if(next != null){
            LockSupport.unpark(next.thread);
        }
    }

    class Node{
        Node pre;
        Node next;
        Thread thread;
    }
}
