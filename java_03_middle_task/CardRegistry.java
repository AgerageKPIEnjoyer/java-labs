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
     */
    public SkiPassCard issuePass(PassType type, LocalDate startDate) {
        LocalDate validFrom = startDate;
        LocalDate validTo;

        if (type.getMode() == PassMode.RIDE_COUNT || type.isSeasonal()) {
            validTo = seasonEndDate;
        } else {
            validTo = startDate.plusDays(type.getValidDays() - 1L);
        }

        String id = "SKI-" + String.format("%06d", sequence.getAndIncrement());
        SkiPassCard card = new SkiPassCard(id, type, startDate, validFrom, validTo);
        cardsById.put(id, card);
        return card;
    }

    /** Blocks the card with the given id. Returns false if no such card is registered. */
    public boolean blockPass(String id) {
        SkiPassCard card = cardsById.get(id);
        if (card == null) {
            return false;
        }
        card.block();
        return true;
    }

    public Optional<SkiPassCard> findCard(String id) {
        return Optional.ofNullable(cardsById.get(id));
    }

    public Map<String, SkiPassCard> getAllCards() {
        return cardsById;
    }
}
