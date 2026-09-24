import java.time.LocalTime;

/**
 * Every ski-pass product the resort sells. Each constant carries the data
 * needed to compute a card's validity window and ride allowance when it is
 * issued, and the rules the turnstile checks against on each entry attempt.
 */
public enum PassType {

    // ---- Weekday: unlimited rides, time-based ----
    WEEKDAY_HALF_DAY_MORNING("Weekday half-day (09:00-13:00)",
            PassCategory.WEEKDAY, PassMode.TIME_UNLIMITED, 1, LocalTime.of(9, 0), LocalTime.of(13, 0), 0),
    WEEKDAY_HALF_DAY_AFTERNOON("Weekday half-day (13:00-17:00)",
            PassCategory.WEEKDAY, PassMode.TIME_UNLIMITED, 1, LocalTime.of(13, 0), LocalTime.of(17, 0), 0),
    WEEKDAY_ONE_DAY("Weekday 1-day", PassCategory.WEEKDAY, PassMode.TIME_UNLIMITED, 1, null, null, 0),
    WEEKDAY_TWO_DAY("Weekday 2-day", PassCategory.WEEKDAY, PassMode.TIME_UNLIMITED, 2, null, null, 0),
    WEEKDAY_FIVE_DAY("Weekday 5-day", PassCategory.WEEKDAY, PassMode.TIME_UNLIMITED, 5, null, null, 0),

    // ---- Weekday: ride-count based ----
    WEEKDAY_10_RIDES("Weekday 10 rides", PassCategory.WEEKDAY, PassMode.RIDE_COUNT, 0, null, null, 10),
    WEEKDAY_20_RIDES("Weekday 20 rides", PassCategory.WEEKDAY, PassMode.RIDE_COUNT, 0, null, null, 20),
    WEEKDAY_50_RIDES("Weekday 50 rides", PassCategory.WEEKDAY, PassMode.RIDE_COUNT, 0, null, null, 50),
    WEEKDAY_100_RIDES("Weekday 100 rides", PassCategory.WEEKDAY, PassMode.RIDE_COUNT, 0, null, null, 100),

    // ---- Weekend: unlimited rides, time-based ----
    WEEKEND_HALF_DAY_MORNING("Weekend half-day (09:00-13:00)",
            PassCategory.WEEKEND, PassMode.TIME_UNLIMITED, 1, LocalTime.of(9, 0), LocalTime.of(13, 0), 0),
    WEEKEND_HALF_DAY_AFTERNOON("Weekend half-day (13:00-17:00)",
            PassCategory.WEEKEND, PassMode.TIME_UNLIMITED, 1, LocalTime.of(13, 0), LocalTime.of(17, 0), 0),
    WEEKEND_ONE_DAY("Weekend 1-day", PassCategory.WEEKEND, PassMode.TIME_UNLIMITED, 1, null, null, 0),
    WEEKEND_TWO_DAY("Weekend 2-day", PassCategory.WEEKEND, PassMode.TIME_UNLIMITED, 2, null, null, 0),

    // ---- Weekend: ride-count based ----
    WEEKEND_10_RIDES("Weekend 10 rides", PassCategory.WEEKEND, PassMode.RIDE_COUNT, 0, null, null, 10),
    WEEKEND_20_RIDES("Weekend 20 rides", PassCategory.WEEKEND, PassMode.RIDE_COUNT, 0, null, null, 20),
    WEEKEND_50_RIDES("Weekend 50 rides", PassCategory.WEEKEND, PassMode.RIDE_COUNT, 0, null, null, 50),
    WEEKEND_100_RIDES("Weekend 100 rides", PassCategory.WEEKEND, PassMode.RIDE_COUNT, 0, null, null, 100),

    // ---- Seasonal ----
    SEASONAL("Seasonal pass", PassCategory.SEASONAL, PassMode.TIME_UNLIMITED, 0, null, null, 0);

    private final String displayName;
    private final PassCategory category;
    private final PassMode mode;
    /** Number of calendar days the pass is valid for (only meaningful for TIME_UNLIMITED, non-seasonal types). */
    private final int validDays;
    /** Non-null only for half-day passes: the allowed clock-time window. */
    private final LocalTime windowStart;
    private final LocalTime windowEnd;
    /** Number of rides sold (only meaningful for RIDE_COUNT types). */
    private final int totalRides;

    PassType(String displayName, PassCategory category, PassMode mode, int validDays,
             LocalTime windowStart, LocalTime windowEnd, int totalRides) {
        this.displayName = displayName;
        this.category = category;
        this.mode = mode;
        this.validDays = validDays;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.totalRides = totalRides;
    }

    public String getDisplayName() {
        return displayName;
    }

    public PassCategory getCategory() {
        return category;
    }

    public PassMode getMode() {
        return mode;
    }

    public int getValidDays() {
        return validDays;
    }

    public boolean hasTimeWindow() {
        return windowStart != null;
    }

    public LocalTime getWindowStart() {
        return windowStart;
    }

    public LocalTime getWindowEnd() {
        return windowEnd;
    }

    public int getTotalRides() {
        return totalRides;
    }

    public boolean isSeasonal() {
        return category == PassCategory.SEASONAL;
    }
}
