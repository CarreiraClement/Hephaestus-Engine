package fr.olympus.hephaestus.processing;

import fr.olympus.hephaestus.materials.MaterialInstance;
import fr.olympus.hephaestus.register.RecipeSelector;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * A default implementation of ProcessRecipe with common logic.
 */
public abstract class DefaultProcessRecipe implements ProcessRecipe {

    protected String id;
    protected RecipeSelector selector;
    protected final boolean ordered;

    protected final List<MaterialMatcher> inputs;
    protected final List<MaterialMatcher> outputs;
    protected final List<Integer> cost;

    protected final TimeWindow window;


    /**
     * Constructor.
     *
     * @param ordered Whether the inputs are ordered.
     * @param inputs  Input material matchers.
     * @param outputs Output material matchers.
     * @param window  Time window, or null for manual processes.
     */
    protected DefaultProcessRecipe(boolean ordered,
                                   List<MaterialMatcher> inputs,
                                   List<MaterialMatcher> outputs,
                                   TimeWindow window) {
        this.ordered = ordered;
        this.inputs = List.copyOf(inputs);
        this.outputs = List.copyOf(outputs);
        this.cost = this.inputs.stream().map(MaterialMatcher::getQuantity).toList(); // simple: cost = number of inputs
        this.window = window;
    }


    /**
     * Recipe identifier.
     *
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

            return true;
        }
        return !window.beforeMin(elapsedSeconds);
    }

    @Override
    public final void registerMeta(String registerId, RecipeSelector selector) {
        this.id = Objects.requireNonNull(registerId, "Recipe ID cannot be null");
        this.selector = Objects.requireNonNull(selector, "RecipeSelector cannot be null");
    }

    /**
     * Checks if the given material ID matches the given matcher.
     *
     * @param matcher    Material matcher.
     * @param materialId Material ID to check.
     * @param data       Hephaestus data for category lookups.
     * @return True if the material ID matches the matcher, false otherwise.
     */
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
