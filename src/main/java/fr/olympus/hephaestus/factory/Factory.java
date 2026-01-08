package fr.olympus.hephaestus.factory;

import fr.olympus.hephaestus.materials.MaterialInstance;
import fr.olympus.hephaestus.processing.*;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public abstract class Factory {

    protected final List<MaterialInstance> contents = new ArrayList<>();
    protected final List<MaterialInstance> outputs = new ArrayList<>();

    protected final List<ProcessRecipe> recipes = new ArrayList<>();

    protected boolean isOperating;

    private ProcessSession session;

    // --- Registry meta (set by HephaestusData.createFactory) ---
    private String registryId;
    private Set<String> registryGroups = Set.of();
    private int registryLevel;

    public final String getRegistryId() { return registryId; }
    public final Set<String> getRegistryGroups() { return registryGroups; }
    public final int getRegistryLevel() { return registryLevel; }

    public void startFactory() { isOperating = true; }
    public void stopFactory() { isOperating = false; session = null; }

    public void addRecipes(List<ProcessRecipe> list) {
        if (list != null) recipes.addAll(list);
    }

    public List<MaterialInstance> extractAllOutputs() {
        List<MaterialInstance> out = new ArrayList<>(outputs);
        outputs.clear();
        return out;
    }

    public void insert(MaterialInstance mat) {
        contents.add(mat);
    }

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

    /** Appelé par la lib (HephaestusData) au moment de la création de l'instance runtime. */
    public final void setRegistryMeta(String id, Set<String> groups, int level) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be null/blank.");
        if (groups == null) throw new IllegalArgumentException("groups cannot be null.");
        this.registryId = id;
        this.registryGroups = Set.copyOf(groups);
        this.registryLevel = level;
    }

    private static final class ProcessSession {
        final ProcessRecipe recipe;
        float elapsed;

        ProcessSession(ProcessRecipe recipe) {
            this.recipe = recipe;
            this.elapsed = 0f;
        }

        ProcessingPhase phase() {
            TimeWindow w = recipe.timeWindowOrNull();
            if (w == null) return ProcessingPhase.IN_WINDOW;
            if (w.beforeMin(elapsed)) return ProcessingPhase.BEFORE_MIN;
            if (w.afterMax(elapsed)) return ProcessingPhase.AFTER_MAX;
            return ProcessingPhase.IN_WINDOW;
        }
    }
}
