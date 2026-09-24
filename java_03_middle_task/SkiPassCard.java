import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Data stored on (and read from) a single ski-pass card: a unique
 * identifier, the pass type, the validity period, the number of rides
 * remaining (only meaningful for ride-count passes) and whether the card
 * has been blocked.
 */
public class SkiPassCard {

    private final String id;
    private final PassType type;
    private final LocalDate issueDate;
    private final LocalDate validFrom;
    private final LocalDate validTo;

    /** Null for TIME_UNLIMITED passes (they have no ride counter). */
    private Integer ridesRemaining;

    private boolean blocked = false;

    /**
     * Simulates the physical/chip read outcome. In real life a damaged or
     * demagnetised card can fail to be read even though the registry still
     * has a record of it; here it defaults to true and can be forced to
     * false to simulate a bad read for a single entry attempt (see
     * Turnstile.attemptEntry's forceUnreadable parameter) without
     * permanently altering the card.
     */
    public SkiPassCard(String id, PassType type, LocalDate issueDate, LocalDate validFrom, LocalDate validTo) {
        this.id = id;
        this.type = type;
        this.issueDate = issueDate;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.ridesRemaining = (type.getMode() == PassMode.RIDE_COUNT) ? type.getTotalRides() : null;
    }

    public String getId() {
        return id;
    }

    public PassType getType() {
        return type;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    /** Rides left, or null if this pass is time-unlimited (no ride counter). */
    public Integer getRidesRemaining() {
        return ridesRemaining;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void block() {
        this.blocked = true;
    }

    public boolean isRideBased() {
        return type.getMode() == PassMode.RIDE_COUNT;
    }

    /** True unless this is a ride-count pass that has run out of rides. */
    public boolean hasRidesRemaining() {
        return !isRideBased() || (ridesRemaining != null && ridesRemaining > 0);
    }

    /** Consumes one ride; no-op for time-unlimited passes. */
    public void deductRide() {
        if (isRideBased() && ridesRemaining != null && ridesRemaining > 0) {
            ridesRemaining--;
        }
    }

    public boolean isWithinValidityPeriod(LocalDate date) {
        return !date.isBefore(validFrom) && !date.isAfter(validTo);
    }

    public boolean isDayTypeAllowed(LocalDate date) {
        return type.getCategory().isUsableOn(date);
    }

    /** For half-day passes: whether the given moment falls inside the allowed clock-time window. */
    public boolean isWithinTimeWindow(LocalDateTime moment) {
        if (!type.hasTimeWindow()) {
            return true; // no hourly restriction for this pass type
        }
        LocalTime t = moment.toLocalTime();
        return !t.isBefore(type.getWindowStart()) && t.isBefore(type.getWindowEnd());
    }

    @Override
    public String toString() {
        String rides = isRideBased() ? (ridesRemaining + "/" + type.getTotalRides() + " rides left") : "unlimited rides";
        return String.format("Card[%s] %s | valid %s..%s | %s | %s",
                id, type.getDisplayName(), validFrom, validTo, rides, blocked ? "BLOCKED" : "active");
    }
}
