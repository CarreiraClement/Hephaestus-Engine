package fr.olympus.hephaestus.factory;

import fr.olympus.hephaestus.materials.MaterialInstance;
import fr.olympus.hephaestus.processing.*;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Represents a factory that processes materials based on defined recipes.
 */
public abstract class Factory {

    /**
     * The current contents (input materials) of the factory.
     */
    protected final List<MaterialInstance> contents = new ArrayList<>();
    /**
     * The current outputs (produced materials) of the factory.
     */
    protected final List<MaterialInstance> outputs = new ArrayList<>();
    /**
     * The list of processing recipes available to this factory.
     */

    protected final List<ProcessRecipe> recipes = new ArrayList<>();

    /**
     * Indicates whether the factory is currently operating.
     */
    protected boolean isOperating;

    /**
     * The current processing session, if any.
     */
    private ProcessSession session;

    // --- Registry meta (set by HephaestusData.createFactory) ---*
    /**
     * The unique identifier for this factory in the registry.
     */
    private String registryId;

    /**
     * The groups associated with this factory in the registry.
     */
    private Set<String> registryGroups = Set.of();

    /**
     * The level of this factory in the registry.
     */
    private int registryLevel;

    /**
     * Constructs a new Factory instance.
     */
    protected Factory() {
        this.isOperating = false;
        this.session = null;
    }

    /**
     * Gets the registry ID of the factory.
     *
     * @return The registry ID.
     */
    public final String getRegistryId() {
        return registryId;
    }

    /**
     * Gets the registry groups of the factory.
     *
     * @return The registry groups.
     */
    public final Set<String> getRegistryGroups() {
        return registryGroups;
    }

    /**
     * Gets the registry level of the factory.
     *
     * @return The registry level.
     */
    public final int getRegistryLevel() {
        return registryLevel;
    }


    /**
     * Starts the factory's operation.
     */
    public void startFactory() {
        isOperating = true;
    }

    /**
     * Stops the factory's operation.
     */
    public void stopFactory() {
        isOperating = false;
        session = null;
    }

    /**
     * Adds a list of processing recipes to the factory.
     *
     * @param list The list of ProcessRecipe to add.
     */
    public void addRecipes(List<ProcessRecipe> list) {
        if (list != null) recipes.addAll(list);
    }

    /**
     * Gets the list of processing recipes available to the factory.
     *
     * @return The list of ProcessRecipe.
     */
    public List<ProcessRecipe> getRecipes() {
        return recipes;
    }

    /**
     * Extracts and clears all output materials from the factory.
     *
     * @return A list of MaterialInstance representing the outputs.
     */
    public List<MaterialInstance> extractAllOutputs() {
        List<MaterialInstance> out = new ArrayList<>(outputs);
        outputs.clear();
        return out;
    }

    /**
     * Inserts a material instance into the factory's contents.
     *
     * @param mat The MaterialInstance to insert.
     */
    public void insert(MaterialInstance mat) {
        contents.add(mat);
    }

    /**
     * Pushes an event to the factory's processing session.
     *
     * @param event The FactoryEvent to push.
     * @param data  The HephaestusData context.
     */
    public void pushEvent(FactoryEvent event, HephaestusData data) {
        if (!isOperating) return;

        ensureSession(data);
        if (session == null) return;

        ProcessContext ctx = new ProcessContext(contents, outputs);
        ProcessingPhase phase = session.phase();
        session.recipe.onEvent(ctx, data, event, session.elapsed, phase);

        if (session.recipe.tryComplete(ctx, data, session.elapsed, phase)) {
            session = null;
        }
    }

    /**
     * Updates the factory's processing session.
     *
     * @param dt   The delta time since the last update.
     * @param data The HephaestusData context.
     */
    public final void update(float dt, HephaestusData data) {
        if (!isOperating) return;

        ensureSession(data);
        if (session == null) return;

        session.elapsed += dt;

        TimeWindow w = session.recipe.timeWindowOrNull();
        ProcessingPhase phase = session.phase();

        ProcessContext ctx = new ProcessContext(contents, outputs);

        if (w != null) {
            session.recipe.onTick(ctx, data, session.elapsed, phase);

            if (phase == ProcessingPhase.AFTER_MAX) {
                session.recipe.onOverProcessed(ctx, data, session.elapsed);
            }
        }

        if (session.recipe.tryComplete(ctx, data, session.elapsed, phase)) {
            session = null;
        }
    }

    /**
     * Ensures that there is an active processing session.
     *
     * @param data The HephaestusData context.
     */
    private void ensureSession(HephaestusData data) {
        if (session != null) return;

        ProcessContext ctx = new ProcessContext(contents, outputs);

        ProcessRecipe best = recipes.stream()
                .filter(r -> r.canStart(ctx, data))
                .max(Comparator
                        .comparingInt(ProcessRecipe::priority)
                        .thenComparingInt(ProcessRecipe::specificityScore)
                        .thenComparingInt(ProcessRecipe::inputCount))
                .orElse(null);

        if (best != null) {
            session = new ProcessSession(best);
        }
    }

    /**
     * Called by the library (HephaestusData) at the time of creating the runtime instance.
     *
     * @param id     The registry ID to set.
     * @param groups The registry groups to set.
     * @param level  The registry level to set.
     */
    public final void setRegistryMeta(String id, Set<String> groups, int level) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be null/blank.");
        if (groups == null) throw new IllegalArgumentException("groups cannot be null.");
        this.registryId = id;
        this.registryGroups = Set.copyOf(groups);
        this.registryLevel = level;
    }


    /**
     * Sets the current processing session for the factory.
     *
     * @param recipe The ProcessRecipe to set for the session.
     */
    public final void setSession(ProcessRecipe recipe) {
        this.session = new ProcessSession(recipe);
    }

    /**
     * Checks if there is an active processing session.
     *
     * @return true if there is an active session, false otherwise.
     */
    public final boolean getSession() {
        return this.session != null;
    }

    /**
     * Represents a processing session within the factory.
     */
    private static final class ProcessSession {

        /**
         * The processing recipe associated with the session.
         */
        final ProcessRecipe recipe;

        /**
         * The elapsed time since the session started.
         */
        float elapsed;

        /**
         * Constructs a ProcessSession with the specified recipe.
         */
        ProcessSession(ProcessRecipe recipe) {
            this.recipe = recipe;
            this.elapsed = 0f;
        }

        /**
         * Determines the current processing phase based on elapsed time and recipe time window.
         */
        ProcessingPhase phase() {
            TimeWindow w = recipe.timeWindowOrNull();
            if (w == null) return ProcessingPhase.IN_WINDOW;
            if (w.beforeMin(elapsed)) return ProcessingPhase.BEFORE_MIN;
            if (w.afterMax(elapsed)) return ProcessingPhase.AFTER_MAX;
            return ProcessingPhase.IN_WINDOW;
        }
    }

}
