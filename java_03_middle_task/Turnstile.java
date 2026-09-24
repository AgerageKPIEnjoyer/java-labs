import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    /** Timestamp of the last processed attempt; the turnstile's own clock cannot move backwards from here. */
    private LocalDateTime lastMoment = null;

    public Turnstile(CardRegistry registry) {
        this.registry = registry;
    }

    /**
     * Attempts entry with the card whose id is given.
     *
     * @param cardId          the id read off the card (full form or a bare
     *                        number; both are accepted, see CardRegistry.normalizeId)
     * @param moment          the date/time of the attempt
     * @param forceUnreadable simulates a hardware read failure on this single
     *                        attempt (e.g. a scratched or demagnetised chip),
     *                        even if the card is otherwise perfectly valid
     * @throws IllegalArgumentException if moment is earlier than the last
     *         attempt this turnstile has processed - a real turnstile's
     *         clock only moves forward, so this signals bad simulator input
     */
    public AccessResult attemptEntry(String cardId, LocalDateTime moment, boolean forceUnreadable) {
        if (lastMoment != null && moment.isBefore(lastMoment)) {
            throw new IllegalArgumentException(
                    "Attempt time " + moment + " is earlier than this turnstile's last recorded " +
                            "attempt (" + lastMoment + "); the turnstile's clock cannot move backwards.");
        }

        String id = CardRegistry.normalizeId(cardId);

        if (forceUnreadable) {
            return finish(id, null, AccessResult.DENIED_UNREADABLE, moment);
        }

        // "If the data cannot be read" also covers a card whose id the
        // registry has no record of: the turnstile has nothing valid to
        // verify against.
        SkiPassCard card = registry.findCard(id).orElse(null);
        if (card == null) {
            return finish(id, null, AccessResult.DENIED_UNREADABLE, moment);
        }

        AccessResult result = verify(card, moment);

        if (result == AccessResult.GRANTED) {
            card.deductRide(); // no-op for time-unlimited passes
        }

        return finish(id, card.getType(), result, moment);
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
        lastMoment = moment;
        return result;
    }

    /** Timestamp of the last attempt this turnstile has processed, if any. */
    public Optional<LocalDateTime> getLastMoment() {
        return Optional.ofNullable(lastMoment);
    }

    public TurnstileStatistics getStatistics() {
        return statistics;
    }

    public List<AccessLogEntry> getLog() {
        return log;
    }
}
