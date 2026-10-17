package buffer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CircularBuffer<T> {

    private final Object[] items;
    private int head; // index of the next element to read
    private int tail; // index of the next free slot to write
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public CircularBuffer(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1");
        }
        // One slot always stays unused, so head == tail can only mean "empty".
        items = new Object[capacity + 1];
    }

    /** Adds an element; waits while the buffer is full. */
    public void put(T item) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (isFull()) {
                notFull.await();
            }
            items[tail] = item;
            tail = next(tail);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /** Removes the oldest element; waits while the buffer is empty. */
    @SuppressWarnings("unchecked")
    public T take() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (isEmpty()) {
                notEmpty.await();
            }
            T item = (T) items[head];
            items[head] = null; // do not keep a reference to a consumed element
            head = next(head);
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return (tail - head + items.length) % items.length;
        } finally {
            lock.unlock();
        }
    }

    // The private helpers below must be called with the lock held.
    private boolean isEmpty() {
        return head == tail;
    }

    private boolean isFull() {
        return next(tail) == head;
    }

    private int next(int index) {
        return (index + 1) % items.length; // wraps back to 0 after the last slot
    }
}
