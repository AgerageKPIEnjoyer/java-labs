/**
 * How a pass grants access:
 *   TIME_UNLIMITED - unlimited rides within a validity window (half-day/1/2/5-day/season).
 *   RIDE_COUNT     - a fixed number of rides is consumed one at a time.
 */
public enum PassMode {
    TIME_UNLIMITED,
    RIDE_COUNT
}
