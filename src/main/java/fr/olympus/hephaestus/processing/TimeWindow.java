package fr.olympus.hephaestus.processing;

public record TimeWindow(float minSeconds, float maxSeconds) {
    public TimeWindow {
        if (minSeconds < 0) throw new IllegalArgumentException("minSeconds < 0");
        if (maxSeconds < minSeconds) throw new IllegalArgumentException("maxSeconds < minSeconds");
    }

    public boolean inWindow(float t) {
        return t >= minSeconds && t <= maxSeconds;
    }

    public boolean beforeMin(float t) {
        return t < minSeconds;
    }

    public boolean afterMax(float t) {
        return t > maxSeconds;
    }
}
