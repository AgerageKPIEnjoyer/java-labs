import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The back-office system the turnstile is linked to: it is the source of
 * truth for which cards have been issued, and it is the only place a card
 * can be blocked from. The turnstile consults it (indirectly, through the
 * SkiPassCard objects it hands out) rather than trusting an unregistered
 * card's self-reported data.
 */
public class CardRegistry {

    private final Map<String, SkiPassCard> cardsById = new LinkedHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1);
    private final LocalDate seasonEndDate;

    public CardRegistry(LocalDate seasonEndDate) {
        this.seasonEndDate = seasonEndDate;
    }

    /**
     * Issues a new pass of the given type, starting on startDate, and adds
     * it to the registry. The validity window is derived from the pass
     * type's rules: a fixed number of days for time-unlimited weekday/
     * weekend passes, or "until the end of the season" for ride-count and
     * seasonal passes.
     *
     * @throws IllegalArgumentException if startDate is in the past, is
     *         after the end of the season, or (for fixed-length passes)
     *         would make the pass run past the end of the season
     */
    public SkiPassCard issuePass(PassType type, LocalDate startDate) {
        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today)) {
            throw new IllegalArgumentException(
                    "Start date cannot be in the past (today is " + today + ").");
        }
        if (startDate.isAfter(seasonEndDate)) {
            throw new IllegalArgumentException(
                    "Start date is after the end of the season (" + seasonEndDate + ").");
        }

        LocalDate validFrom = startDate;
        LocalDate validTo;

        if (type.getMode() == PassMode.RIDE_COUNT || type.isSeasonal()) {
            validTo = seasonEndDate;
        } else {
            validTo = startDate.plusDays(type.getValidDays() - 1L);
            if (validTo.isAfter(seasonEndDate)) {
                throw new IllegalArgumentException(
                        "A " + type.getDisplayName() + " pass starting " + startDate +
                                " would run until " + validTo + ", past the end of the season (" +
                                seasonEndDate + "). Choose an earlier start date.");
            }
        }

        String id = "SKI-" + String.format("%06d", sequence.getAndIncrement());
        SkiPassCard card = new SkiPassCard(id, type, startDate, validFrom, validTo);
        cardsById.put(id, card);
        return card;
    }

    public LocalDate getSeasonEndDate() {
        return seasonEndDate;
    }

    /**
     * Normalizes a user-supplied card identifier so that the full form
     * ("SKI-000007"), a bare number ("7"), and a zero-padded number
     * ("000007") all resolve to the same card, and so lookups are not
     * case-sensitive.
     */
    public static String normalizeId(String rawId) {
        if (rawId == null) {
            return null;
        }
        String trimmed = rawId.trim();
        if (trimmed.matches("\\d+")) {
            try {
                long n = Long.parseLong(trimmed);
                return "SKI-" + String.format("%06d", n);
            } catch (NumberFormatException e) {
                // Too large to be a real sequence value; fall through and
                // treat it as a literal (non-matching) id below.
            }
        }
        return trimmed.toUpperCase();
    }

    /** Blocks the card with the given id (full id or bare number). Returns false if no such card is registered. */
    public boolean blockPass(String id) {
        SkiPassCard card = cardsById.get(normalizeId(id));
        if (card == null) {
            return false;
        }
        card.block();
        return true;
    }

    public Optional<SkiPassCard> findCard(String id) {
        return Optional.ofNullable(cardsById.get(normalizeId(id)));
    }

    public Map<String, SkiPassCard> getAllCards() {
        return cardsById;
    }
}
