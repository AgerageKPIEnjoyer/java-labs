package buffer;

public class MessageTransfer implements Runnable {

    private final int number;
    private final CircularBuffer<String> input;
    private final CircularBuffer<String> output;

    public MessageTransfer(int number, CircularBuffer<String> input, CircularBuffer<String> output) {
        this.number = number;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = input.take();
                output.put("Thread No. " + number + " transferred message \"" + message + "\"");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
