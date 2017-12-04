package org.hallemojah.frontend;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockUtils {

    static class TimedCV implements Comparable<TimedCV> {
        public Condition cv;
        public Integer sequence_number;
        public boolean isGet;

        public TimedCV(Condition cv, Integer sequence_number, boolean isGet) {
            this.cv = cv;
            this.sequence_number = sequence_number;
            this.isGet = isGet;
        }

        public int compareTo(TimedCV other) {
            return sequence_number.compareTo(other.sequence_number);
        }
    }

    static class Status {
        public ReentrantLock lock;
        public PriorityBlockingQueue<TimedCV> operations; // This is removed halfway executing in order to do get chaining
        public Integer next_seq; // This is updated only when an operation is ended, or the order will be wrong

        public Status() {
            lock = new ReentrantLock();
            operations = new PriorityBlockingQueue<TimedCV>();
            next_seq = 1;
        }
    }

    static class lockStatusCV {
        public Lock lock;
        public Status status;
        public Condition cv;
        public String uuid;

        public lockStatusCV(Lock lock, Status status, Condition cv, String uuid) {
            this.lock = lock;
            this.status = status;
            this.cv = cv;
            this.uuid = uuid;
        }
    }

    static ConcurrentHashMap<String, Status> uuid_status_map = new ConcurrentHashMap<String, Status>();

    public static lockStatusCV addToOp(String uuid, Integer sequence_number, boolean isGet) {
        uuid_status_map.putIfAbsent(uuid, new Status());
        Status status = uuid_status_map.get(uuid);
        ReentrantLock lock = status.lock;
        Condition thisCV = lock.newCondition();
        TimedCV thisTimedCV = new TimedCV(thisCV, sequence_number, isGet);
        status.operations.add(thisTimedCV);
        return new lockStatusCV(lock, status, thisCV, uuid);
    }

    public static void acquire_lock_common(lockStatusCV bundle, Integer sequence_number) {
        // Should be while here, it's hacky since I sometimes woke myslef because of chaining
        if (!sequence_number.equals(bundle.status.next_seq)) {
            try {
//                System.out.println("Add to wait: " + sequence_number + " of " + bundle.uuid);
                bundle.cv.await();
//                System.out.println(sequence_number + " of " + bundle.uuid + " Woken");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
//            System.out.println(sequence_number + " of " + bundle.uuid + " just go without any blocking");
        }
    }

    public static void acquire_lock_write(lockStatusCV bundle, Integer sequence_number) {
        bundle.lock.lock();
        acquire_lock_common(bundle, sequence_number);
        bundle.status.operations.poll();
        bundle.lock.unlock();
    }

    public static void acquire_lock_read(lockStatusCV bundle, Integer sequence_number) {
        bundle.lock.lock();
        acquire_lock_common(bundle, sequence_number);
        // Called by normal method, thus it's first called then poll
        if (bundle.status.operations.peek() != null && bundle.status.operations.peek().sequence_number.equals(sequence_number)) {
            bundle.status.operations.poll(); //Remove current
            Integer local_next_seq = sequence_number + 1;
            while (bundle.status.operations.peek() != null && bundle.status.operations.peek().isGet && bundle.status.operations.peek().sequence_number.equals(local_next_seq)) {
//                System.out.println(sequence_number + " chaining get " + bundle.status.operations.peek().sequence_number + " of " + bundle.uuid);
                // Chaining get is first poll than called
                bundle.status.operations.poll().cv.signal();
                local_next_seq++;
            }
        } else {
//            System.out.println(sequence_number + " of " + bundle.uuid + ": I am fired by chaining");
        }
        bundle.lock.unlock();
    }

    public static void release_lock(lockStatusCV bundle, Integer sequence_number) {
        bundle.lock.lock();
        if (sequence_number + 1 > bundle.status.next_seq) {
            bundle.status.next_seq = sequence_number + 1;
        }
        TimedCV nextTimedCV = bundle.status.operations.peek();
        if (nextTimedCV != null && nextTimedCV.sequence_number.equals(bundle.status.next_seq)) {
//            System.out.println("Signaled " + nextTimedCV.sequence_number + " of " + bundle.uuid);
            nextTimedCV.cv.signal();
        }
        bundle.lock.unlock();
    }

}
