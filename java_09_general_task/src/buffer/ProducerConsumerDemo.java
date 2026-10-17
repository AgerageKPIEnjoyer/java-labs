package buffer;

public class ProducerConsumerDemo {

    private static final int BUFFER_CAPACITY = 10;
    private static final int PRODUCERS = 5;
    private static final int TRANSFERS = 2;
    private static final int MESSAGES_TO_READ = 100;

    public static void main(String[] args) throws InterruptedException {
        CircularBuffer<String> first = new CircularBuffer<>(BUFFER_CAPACITY);
        CircularBuffer<String> second = new CircularBuffer<>(BUFFER_CAPACITY);

        for (int i = 1; i <= PRODUCERS; i++) {
            startDaemon(new MessageProducer(i, first), "producer-" + i);
        }
        for (int i = 1; i <= TRANSFERS; i++) {
            startDaemon(new MessageTransfer(PRODUCERS + i, first, second), "transfer-" + i);
        }

        for (int i = 1; i <= MESSAGES_TO_READ; i++) {
            System.out.println(i + ": " + second.take());
        }
        // main ends here; the daemon threads do not keep the JVM alive
    }

    private static void startDaemon(Runnable task, String name) {
        Thread thread = new Thread(task, name);
        thread.setDaemon(true); // must be set before start()
        thread.start();
    }
}
