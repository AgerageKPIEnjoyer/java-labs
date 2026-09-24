import java.util.EnumMap;
import java.util.Map;

/**
 * Aggregate and per-pass-type counters for granted/denied access attempts,
 * plus an "unreadable/unrecognized" bucket for attempts where the pass type
 * could not even be determined.
 */
public class TurnstileStatistics {

    /** Per-pass-type granted/denied counts. */
    public static class Counts {
        private int granted = 0;
        private int denied = 0;

        public int getGranted() {
            return granted;
        }

        public int getDenied() {
            return denied;
        }

        public int getTotal() {
            return granted + denied;
        }
    }

    private final Map<PassType, Counts> byType = new EnumMap<>(PassType.class);
    private final Counts unattributed = new Counts(); // unreadable / unrecognized cards

    private int totalGranted = 0;
    private int totalDenied = 0;

    public void record(AccessResult result, PassType passType) {
        if (result.isGranted()) {
            totalGranted++;
        } else {
            totalDenied++;
        }

        Counts counts = (passType != null)
                ? byType.computeIfAbsent(passType, t -> new Counts())
                : unattributed;

        if (result.isGranted()) {
            counts.granted++;
        } else {
            counts.denied++;
        }
    }

    public int getTotalGranted() {
        return totalGranted;
    }

    public int getTotalDenied() {
        return totalDenied;
    }

    public int getTotalAttempts() {
        return totalGranted + totalDenied;
    }

    public Map<PassType, Counts> getByType() {
        return byType;
    }

    public Counts getUnattributed() {
        return unattributed;
    }

    public void printAggregateReport() {
        System.out.println("=== Aggregate access statistics ===");
        System.out.println("Total attempts : " + getTotalAttempts());
        System.out.println("Granted        : " + totalGranted);
        System.out.println("Denied         : " + totalDenied);
    }

    public void printByTypeReport() {
        System.out.println("=== Access statistics by pass type ===");
        if (byType.isEmpty() && unattributed.getTotal() == 0) {
            System.out.println("No attempts recorded yet.");
            return;
        }
        System.out.printf("%-32s %8s %8s %8s%n", "Pass type", "Granted", "Denied", "Total");
        for (PassType type : PassType.values()) {
            Counts c = byType.get(type);
            if (c == null || c.getTotal() == 0) {
                continue;
            }
            System.out.printf("%-32s %8d %8d %8d%n", type.getDisplayName(), c.getGranted(), c.getDenied(), c.getTotal());
        }
        if (unattributed.getTotal() > 0) {
            System.out.printf("%-32s %8d %8d %8d%n", "Unreadable / unrecognized",
                    unattributed.getGranted(), unattributed.getDenied(), unattributed.getTotal());
        }
    }
}
