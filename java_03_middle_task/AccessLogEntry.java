import java.time.LocalDateTime;

/**
 * A single logged access attempt. passType is null when the card could not
 * be read/identified, since in that case the turnstile has no reliable data
 * to attribute the attempt to a specific pass type.
 */
public class AccessLogEntry {

    private final LocalDateTime timestamp;
    private final String cardId;      // may be "UNKNOWN" if unreadable
    private final PassType passType;  // may be null if unreadable
    private final AccessResult result;

    public AccessLogEntry(LocalDateTime timestamp, String cardId, PassType passType, AccessResult result) {
        this.timestamp = timestamp;
        this.cardId = cardId;
        this.passType = passType;
        this.result = result;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getCardId() {
        return cardId;
    }

    public PassType getPassType() {
        return passType;
    }

    public AccessResult getResult() {
        return result;
    }

    @Override
    public String toString() {
        String typeLabel = (passType != null) ? passType.getDisplayName() : "unknown/unreadable";
        return String.format("[%s] card=%s type=%s -> %s", timestamp, cardId, typeLabel, result);
    }
}
