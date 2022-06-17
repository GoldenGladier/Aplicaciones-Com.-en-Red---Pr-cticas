 import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
 
public class ReentrantLockTest {
    public static void main(String[] args) {
 
        Lock lock=new ReentrantLock(true);
        //ReentrantLock lock1 = new ReentrantLock();
        MyRunnable myRunnable=new MyRunnable(lock);
        new Thread(myRunnable,"Thread-1").start();
        new Thread(myRunnable,"Thread-2").start();
        
    }
}
 
 


class MyRunnable implements Runnable{
    
    Lock lock;
    public MyRunnable(Lock lock) { 
        this.lock=lock;
    }
    
    public void run(){
        
       System.out.println(Thread.currentThread().getName()+" esperando por el monitor de acceso"); 
       try{
        lock.lock();
        System.out.println();
        System.out.println(Thread.currentThread().getName()+" ha llamado lock(), lockHoldCount=1 ");    
        lock.lock();         
        System.out.println(Thread.currentThread().getName()+" ha llamado lock(), lockHoldCount=2 ");
        
       }finally{
        lock.unlock();    
        System.out.println(Thread.currentThread().getName()+" llamando unlock(), lockHoldCount sera 1 ");
        lock.unlock();    
        System.out.println(Thread.currentThread().getName()+" llamando unlock(), lockHoldCount sera 0 ");
       }   
    }
}
 
