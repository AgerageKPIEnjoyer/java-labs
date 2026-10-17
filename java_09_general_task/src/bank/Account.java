package bank;

import java.util.concurrent.atomic.AtomicInteger;

public class Account {

    private static final AtomicInteger NEXT_ID = new AtomicInteger();

    // Unique and immutable: the lock ordering in Bank.transfer() relies on it.
    private final int id = NEXT_ID.getAndIncrement();
    private long balance;

    public Account(long initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance must not be negative");
        }
        this.balance = initialBalance;
    }

    public int getId() {
        return id;
    }

    public synchronized long getBalance() {
        return balance;
    }

    public synchronized void withdraw(int amount) {
        if (amount > balance) {
            throw new IllegalStateException("Insufficient funds on account " + id);
        }
        balance -= amount;
    }

    public synchronized void deposit(int amount) {
        balance += amount;
    }
}