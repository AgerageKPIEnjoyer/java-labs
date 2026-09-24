import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Which days a pass may be used on.
 */
public enum PassCategory {
    WEEKDAY {
        @Override
        public boolean isUsableOn(LocalDate date) {
            DayOfWeek day = date.getDayOfWeek();
            return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
        }
    },
    WEEKEND {
        @Override
        public boolean isUsableOn(LocalDate date) {
            DayOfWeek day = date.getDayOfWeek();
            return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
        }
    },
    SEASONAL {
        @Override
        public boolean isUsableOn(LocalDate date) {
            return true; // no day-of-week restriction
        }
    };

    /** Whether a pass of this category may be used on the given calendar date. */
    public abstract boolean isUsableOn(LocalDate date);
}
