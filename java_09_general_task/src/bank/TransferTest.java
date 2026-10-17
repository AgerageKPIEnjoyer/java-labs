package bank;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Usage: java bank.TransferTest [safe|unsafe]
 */
public class TransferTest {

    private static final int ACCOUNTS = 100;
    private static final int THREADS = 5_000;
    private static final int TRANSFERS_PER_THREAD = 20;
    private static final int MAX_INITIAL_BALANCE = 10_000;
    private static final int MAX_AMOUNT = 5_000;

    public static void main(String[] args) throws InterruptedException {
        Bank bank = new Bank();

        Account[] accounts = new Account[ACCOUNTS];
        for (int i = 0; i < ACCOUNTS; i++) {
            accounts[i] = new Account(ThreadLocalRandom.current().nextInt(1, MAX_INITIAL_BALANCE + 1));
        }
        long totalBefore = total(accounts);

        CountDownLatch startSignal = new CountDownLatch(1);
        AtomicInteger done = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();
        Thread[] threads = new Thread[THREADS];

        for (int t = 0; t < THREADS; t++) {
            threads[t] = new Thread(() -> {
                try {
                    startSignal.await(); // release all threads at once to maximise contention
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                ThreadLocalRandom random = ThreadLocalRandom.current();
                for (int i = 0; i < TRANSFERS_PER_THREAD; i++) {
                    Account from = accounts[random.nextInt(ACCOUNTS)];
                    Account to = accounts[random.nextInt(ACCOUNTS)];
                    int amount = random.nextInt(1, MAX_AMOUNT + 1);
                        if (bank.transfer(from, to, amount)) {
                            done.incrementAndGet();
                        } else {
                            rejected.incrementAndGet();
                        }
                }
            });
            threads[t].start();
        }

        long begin = System.nanoTime();
        startSignal.countDown();
        for (Thread thread : threads) {
            thread.join();
        }
        long elapsedMs = (System.nanoTime() - begin) / 1_000_000;

        long totalAfter = total(accounts);
        long negative = java.util.Arrays.stream(accounts).filter(a -> a.getBalance() < 0).count();

        System.out.println("Accounts / threads:    " + ACCOUNTS + " / " + THREADS);
        System.out.println("Transfers made:        " + done.get());
        System.out.println("Transfers rejected:    " + rejected.get());
        System.out.println("Total before:          " + totalBefore);
        System.out.println("Total after:           " + totalAfter);
        System.out.println("Negative balances:     " + negative);
        System.out.println("Time:                  " + elapsedMs + " ms");
        boolean ok = totalBefore == totalAfter && negative == 0;
        System.out.println(ok ? "RESULT: OK - the total is unchanged" : "RESULT: FAILED - money was created or lost");
        if (!ok) {
            System.exit(1);
        }
    }

    private static long total(Account[] accounts) {
        long sum = 0;
        for (Account account : accounts) {
            sum += account.getBalance();
        }
        return sum;
    }
}