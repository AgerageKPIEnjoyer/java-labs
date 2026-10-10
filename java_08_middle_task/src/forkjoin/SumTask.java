package forkjoin;

import java.util.concurrent.RecursiveTask;

public class SumTask extends RecursiveTask<Long> {

    private static final int THRESHOLD = 20;

    private final int[] array;
    private final int from; // inclusive
    private final int to;   // exclusive

    public SumTask(int[] array, int from, int to) {
        this.array = array;
        this.from = from;
        this.to = to;
    }

    @Override
    protected Long compute() {
        if (to - from < THRESHOLD) {
            long sum = 0;
            for (int i = from; i < to; i++) {
                sum += array[i];
            }
            return sum;
        }

        int mid = (from + to) >>> 1;
        SumTask left = new SumTask(array, from, mid);
        SumTask right = new SumTask(array, mid, to);

        // Run the left half asynchronously and compute the right half in
        // this thread, instead of forking both and idling while they run.
        left.fork();
        long rightSum = right.compute();
        return left.join() + rightSum;
    }
}