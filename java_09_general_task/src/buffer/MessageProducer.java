package buffer;

public class MessageProducer implements Runnable {

    private final int number;
    private final CircularBuffer<String> output;

    public MessageProducer(int number, CircularBuffer<String> output) {
        this.number = number;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            for (int n = 1; ; n++) {
                output.put("Thread No. " + number + " generated message " + n);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
