/**
 * Outcome of a single turnstile access attempt.
 */
public enum AccessResult {
    GRANTED(true, "Access granted"),
    DENIED_UNREADABLE(false, "Card data could not be read"),
    DENIED_BLOCKED(false, "Card is blocked"),
    DENIED_EXPIRED(false, "Card is expired (outside its validity period)"),
    DENIED_WRONG_DAY_TYPE(false, "Pass is not valid on this day (weekday/weekend restriction)"),
    DENIED_OUTSIDE_TIME_WINDOW(false, "Outside the pass's allowed time window"),
    DENIED_NO_RIDES_LEFT(false, "No ride credits remaining");

    private final boolean granted;
    private final String description;

    AccessResult(boolean granted, String description) {
        this.granted = granted;
        this.description = description;
    }

    public boolean isGranted() {
        return granted;
    }

    public String getDescription() {
        return description;
    }
}
