package fr.olympus.hephaestus.processing;

/**
 * A time window in seconds.
 *
 * @param minSeconds minimum time in seconds (inclusive)
 * @param maxSeconds maximum time in seconds (inclusive)
 */
public record TimeWindow(float minSeconds, float maxSeconds) {

    /**
     * Creates a TimeWindow.
     *
     * @param minSeconds minimum time in seconds (inclusive)
     * @param maxSeconds maximum time in seconds (inclusive)
     * @throws IllegalArgumentException if minSeconds inferior 0 or maxSeconds inferior minSeconds
     */
    public TimeWindow {
        if (minSeconds < 0) throw new IllegalArgumentException("minSeconds < 0");
        if (maxSeconds < minSeconds) throw new IllegalArgumentException("maxSeconds < minSeconds");
    }

    /**
     * Checks if a given time is within the time window.
     *
     * @param t time in seconds
     * @return true if t is within [minSeconds, maxSeconds], false otherwise
     */
    public boolean inWindow(float t) {
        return t >= minSeconds && t <= maxSeconds;
    }

    /**
     * Checks if a given time is before the minimum time of the window.
     *
     * @param t time in seconds
     * @return true if t inferior minSeconds, false otherwise
     */
    public boolean beforeMin(float t) {
        return t < minSeconds;
    }

    /**
     * Checks if a given time is after the maximum time of the window.
     *
     * @param t time in seconds
     * @return true if t > maxSeconds, false otherwise
     */
    public boolean afterMax(float t) {
        return t > maxSeconds;
    }
}
