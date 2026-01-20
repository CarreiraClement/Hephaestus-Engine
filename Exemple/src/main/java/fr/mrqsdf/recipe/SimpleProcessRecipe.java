package fr.mrqsdf.recipe;

import fr.olympus.hephaestus.materials.MaterialInstance;
import fr.olympus.hephaestus.processing.*;
import fr.olympus.hephaestus.register.RecipeSelector;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A simple implementation of ProcessRecipe.
 */
public abstract class SimpleProcessRecipe implements ProcessRecipe {

    private final String id;
    private final RecipeSelector selector;
    private final boolean ordered;

    private final List<MaterialMatcher> inputs;
    private final List<MaterialMatcher> outputs;
    private final List<Integer>  cost;

    private final TimeWindow window;

    /** Constructor.
     *
     * @param id       Recipe identifier.
     * @param selector Recipe selector.
     * @param ordered  Whether the inputs are ordered.
     * @param inputs   Input material matchers.
     * @param outputs  Output material matchers.
     * @param cost     Recipe cost.
     * @param window   Time window, or null for manual processes.
     */
    protected SimpleProcessRecipe(String id,
                                  RecipeSelector selector,
                                  boolean ordered,
                                  List<MaterialMatcher> inputs,
                                  List<MaterialMatcher> outputs,
                                  List<Integer>  cost,
                                  TimeWindow window) {
        this.id = Objects.requireNonNull(id, "id");
        this.selector = Objects.requireNonNull(selector, "selector");
        this.ordered = ordered;
        this.inputs = List.copyOf(inputs);
        this.outputs = List.copyOf(outputs);
        this.cost = cost;
        this.window = window;
    }

    // ---- planning ----

    /**
     * Recipe identifier.
     * @return Identifier.
     */
    @Override
    public final String id() {
        return id;
    }

    @Override
    public final RecipeSelector selector() {
        return selector;
    }

    @Override
    public final boolean ordered() {
        return ordered;
    }

    @Override
    public final List<MaterialMatcher> inputs() {
        return inputs;
    }

    @Override
    public final List<MaterialMatcher> outputs() {
        return outputs;
    }

    @Override
    public final List<Integer> cost() {
        return cost;
    }

    // ---- runtime heuristics ----
    @Override
    public int priority() {
        return 100;
    }

    @Override
    public int specificityScore() {
        int s = 0;
        for (MaterialMatcher m : inputs) s += m.specificityScore();
        return s;
    }

    @Override
    public int inputCount() {
        return inputs.size();
    }
    @Override
    public int outputCount() {
        return outputs.size();
    }

    @Override
    public TimeWindow timeWindowOrNull() {
        return window;
    }

    /**
     * Checks whether the process can start with the given context.
     * @param ctx Process context.
     * @param data Hephaestus data.
     * @return True if the process can start.
     */
    @Override
    public boolean canStart(ProcessContext ctx, HephaestusData data) {
        // Démo: si tous les inputs sont présents (unordered)
        List<MaterialInstance> contents = ctx.contents();
        boolean[] used = new boolean[contents.size()];

        for (MaterialMatcher need : inputs) {
            int found = -1;
            for (int i = 0; i < contents.size(); i++) {
                if (used[i]) continue;
                if (matches(need, contents.get(i).materialId(), data)) {
                    found = i;
                    break;
                }
            }
            if (found == -1) return false;
            used[found] = true;
        }
        return true;
    }

    @Override
    public boolean tryComplete(ProcessContext ctx, HephaestusData data, float elapsedSeconds, ProcessingPhase phase) {
        if (window == null) {
            // manuel: dans un vrai jeu tu le ferais via progress/events
            return true;
        }
        // auto: terminé dès que min atteint
        return !window.beforeMin(elapsedSeconds);
    }

    /** Helpers */
    private boolean matches(MaterialMatcher matcher, String materialId, HephaestusData data) {
        return switch (matcher.getKind()) {
            case ANY -> true;
            case ID -> matcher.getMaterialId().equals(materialId);
            case ANY_OF_CATEGORIES -> {
                Set<String> cats = data.getMaterialCategoryKeys(materialId);
                boolean ok = false;
                for (String w : matcher.getCategoryKeys()) {
                    if (cats.contains(w)) {
                        ok = true;
                        break;
                    }
                }
                yield ok;
            }
            case ALL_OF_CATEGORIES -> {
                Set<String> cats = data.getMaterialCategoryKeys(materialId);
                yield cats.containsAll(matcher.getCategoryKeys());
            }
        };
    }


}
