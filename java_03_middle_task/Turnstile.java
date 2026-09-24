import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Simulates the turnstile's embedded processor: reads a card's data,
 * verifies it against the resort's access rules, grants or denies entry
 * accordingly, deducts a ride when applicable, and keeps a log plus
 * running statistics of every attempt.
 */
public class Turnstile {

    private final CardRegistry registry;
    private final TurnstileStatistics statistics = new TurnstileStatistics();
    private final List<AccessLogEntry> log = new ArrayList<>();

    public Turnstile(CardRegistry registry) {
        this.registry = registry;
    }

    /**
     * Attempts entry with the card whose id is given.
     *
     * @param cardId          the id read off the card
     * @param moment          the date/time of the attempt
     * @param forceUnreadable simulates a hardware read failure on this single
     *                        attempt (e.g. a scratched or demagnetised chip),
     *                        even if the card is otherwise perfectly valid
     */
    public AccessResult attemptEntry(String cardId, LocalDateTime moment, boolean forceUnreadable) {
        if (forceUnreadable) {
            return finish(cardId, null, AccessResult.DENIED_UNREADABLE, moment);
        }

        // "If the data cannot be read" also covers a card whose id the
        // registry has no record of: the turnstile has nothing valid to
        // verify against.
        SkiPassCard card = registry.findCard(cardId).orElse(null);
        if (card == null) {
            return finish(cardId, null, AccessResult.DENIED_UNREADABLE, moment);
        }

        AccessResult result = verify(card, moment);

        if (result == AccessResult.GRANTED) {
            card.deductRide(); // no-op for time-unlimited passes
        }

        return finish(cardId, card.getType(), result, moment);
    }

    /** Convenience overload: no simulated read failure. */
    public AccessResult attemptEntry(String cardId, LocalDateTime moment) {
        return attemptEntry(cardId, moment, false);
    }

    /** Runs every verification rule, in order, and returns the first failure (or GRANTED). */
    private AccessResult verify(SkiPassCard card, LocalDateTime moment) {
        if (card.isBlocked()) {
            return AccessResult.DENIED_BLOCKED;
        }
        if (!card.isWithinValidityPeriod(moment.toLocalDate())) {
            return AccessResult.DENIED_EXPIRED;
        }
        if (!card.isDayTypeAllowed(moment.toLocalDate())) {
            return AccessResult.DENIED_WRONG_DAY_TYPE;
        }
        if (!card.isWithinTimeWindow(moment)) {
            return AccessResult.DENIED_OUTSIDE_TIME_WINDOW;
        }
        if (!card.hasRidesRemaining()) {
            return AccessResult.DENIED_NO_RIDES_LEFT;
        }
        return AccessResult.GRANTED;
    }

    private AccessResult finish(String cardId, PassType type, AccessResult result, LocalDateTime moment) {
        statistics.record(result, type);
        log.add(new AccessLogEntry(moment, cardId, type, result));
        return result;
    }

    public TurnstileStatistics getStatistics() {
        return statistics;
    }

    public List<AccessLogEntry> getLog() {
        return log;
    }
}
