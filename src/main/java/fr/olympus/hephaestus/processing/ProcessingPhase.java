package fr.olympus.hephaestus.processing;

/**
 * Phases of processing.
 */
public enum ProcessingPhase {
    /**
     * Before the minimum threshold.
     */
    BEFORE_MIN,
    /**
     * Within the time window.
     */
    IN_WINDOW,
    /**
     * After the maximum threshold.
     */
    AFTER_MAX
}
