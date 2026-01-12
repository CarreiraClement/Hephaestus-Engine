package fr.olympus.hephaestus.processing;

import fr.olympus.hephaestus.register.RecipeSelector;

import java.util.List;

/**
 * A recipe for processing materials in a factory.
 */
public interface ProcessRecipe {

    // ---- planning ----

    /**
     * Unique identifier of the recipe
     *
     * @return unique identifier
     */
    String id();

    /**
     * Selector used to choose this recipe
     *
     * @return recipe selector
     */
    RecipeSelector selector();

    /**
     * Whether the order of inputs matters
     *
     * @return true if ordered
     */
    boolean ordered();

    /**
     * Input material matchers
     *
     * @return list of input matchers
     */
    List<MaterialMatcher> inputs();

    /**
     * Output material matchers
     *
     * @return list of output matchers
     */
    List<MaterialMatcher> outputs();

    /**
     * Energy cost per second
     *
     * @return energy cost per second
     */
    int cost();

    // ---- runtime selection ----

    /**
     * Recipe priority (higher = preferred)
     *
     * @return priority
     */
    int priority();

    /**
     * Recipe specificity (higher = more specific)
     *
     * @return specificity score
     */
    int specificityScore();

    /**
     * Number of inputs required
     *
     * @return number of inputs
     */
    int inputCount();

    /**
     * Number of outputs produced
     *
     * @return number of outputs
     */
    int outputCount();

    /**
     * Optional time window for processing
     *
     * @return time window or null
     */
    TimeWindow timeWindowOrNull();

    /**
     * Check if the process can start
     *
     * @param ctx  Process context
     * @param data Hephaestus data
     * @return true if can start
     */
    boolean canStart(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data);

    /**
     * Tick the process
     *
     * @param ctx            Process context
     * @param data           Hephaestus data
     * @param elapsedSeconds Elapsed seconds since start
     * @param phase          Processing phase
     */
    default void onTick(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data, float elapsedSeconds, ProcessingPhase phase) {
    }

    /**
     * Handle an event during processing
     *
     * @param ctx            Process context
     * @param data           Hephaestus data
     * @param event          Factory event
     * @param elapsedSeconds Elapsed seconds since start
     * @param phase          Processing phase
     */
    default void onEvent(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data, FactoryEvent event, float elapsedSeconds, ProcessingPhase phase) {
    }

    /**
     * Try to complete the process
     *
     * @param ctx            Process context
     * @param data           Hephaestus data
     * @param elapsedSeconds Elapsed seconds since start
     * @param phase          Processing phase
     * @return true if completed
     */
    boolean tryComplete(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data, float elapsedSeconds, ProcessingPhase phase);

    /**
     * Handle over-processing
     *
     * @param ctx            Process context
     * @param data           Hephaestus data
     * @param elapsedSeconds Elapsed seconds since start
     */
    default void onOverProcessed(ProcessContext ctx, fr.olympus.hephaestus.resources.HephaestusData data, float elapsedSeconds) {
    }
}
