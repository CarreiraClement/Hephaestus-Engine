package fr.olympus.hephaestus.planning;


/**
 * Enumeration for the mode of planning.
 */
public enum Mode {
    // --- Types of requests
    /**
     * Retrieve only the best crafting plan.
     */
    BEST_ONLY,
    /**
     * Retrieve the top K crafting plans.
     */
    TOP_K,
    /**
     * Retrieve all possible crafting plans.
     */
    ALL
}