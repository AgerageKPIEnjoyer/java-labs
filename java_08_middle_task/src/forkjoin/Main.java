package forkjoin;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class Main {

    private static final int SIZE = 1_000_000;
    private static final int MAX_VALUE = 100;

    public static void main(String[] args) {
        int[] array = new int[SIZE];
        Random random = new Random();
        for (int i = 0; i < SIZE; i++) {
            array[i] = random.nextInt(MAX_VALUE + 1); // 0..100 inclusive
        }

        ForkJoinPool pool = ForkJoinPool.commonPool();
        long start = System.nanoTime();
        long forkJoinSum = pool.invoke(new SumTask(array, 0, array.length));
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        long sequentialSum = 0;
        for (int value : array) {
            sequentialSum += value;
        }

        System.out.println("Array size:         " + SIZE);
        System.out.println("ForkJoin sum:       " + forkJoinSum + " (" + elapsedMs + " ms, "
                + pool.getParallelism() + " workers)");
        System.out.println("Sequential sum:     " + sequentialSum);
        System.out.println("Results match:      " + (forkJoinSum == sequentialSum));
    }
}