package main.java.com.example.lab.exception;
/**
 * Thrown when the tag name entered as a search criterion is not present
 * in the current dataset.
 */
public class TagNotFoundException extends Exception {
    public TagNotFoundException(String message) {
        super(message);
    }
}
