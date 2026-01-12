package fr.olympus.hephaestus.planning;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.ProcessRecipe;

import java.util.*;

/**
 * Planner Backward:
 * - targets an output (target)
 * - looks for all recipes that can produce this target
 * - recursively plans their inputs
 * - combines the input plans (cross product) => branching
 */
public final class CraftPlanner {

    /**
     * PlanOptions
     *
     * @param maxDepth    maximum depth of dependencies
     * @param maxPlans    global limit of generated plans (especially for ALL)
     * @param deduplicate removes duplicates (steps signature)
     */
    public record PlanOptions(int maxDepth, int maxPlans, boolean deduplicate) {

        /**
         * Creates PlanOptions.
         *
         * @param maxDepth    maximum depth of dependencies
         * @param maxPlans    global limit of generated plans (especially for ALL)
         * @param deduplicate removes duplicates (steps signature)
         */
        public PlanOptions {
            if (maxDepth <= 0) throw new IllegalArgumentException("maxDepth must be > 0.");
            if (maxPlans <= 0) throw new IllegalArgumentException("maxPlans must be > 0.");
        }

        /**
         * Safe default options.
         * @return default PlanOptions
         */
        public static PlanOptions safeDefaults() {
            return new PlanOptions(16, 5000, true);
        }
    }

    /**
     * Planning mode
     *
     * @param recipe the process recipe used in this step
     */
    public record PlanStep(ProcessRecipe recipe) {
        /**
         * Creates a PlanStep.
         *
         * @param recipe the process recipe used in this step
         */
        public PlanStep(ProcessRecipe recipe) {
            this.recipe = Objects.requireNonNull(recipe, "recipe");
        }

        /**
         * String representation of the PlanStep.
         *
         * @return the recipe ID
         */
        @Override
        public String toString() {
            return recipe.id();
        }
    }

    /**
     * Planning mode
     *
     * @param totalCost total cost of the plan
     * @param steps     list of plan steps
     */
    public record CraftPlan(int totalCost, List<PlanStep> steps) {
        /**
         * Creates a CraftPlan.
         *
         * @param totalCost total cost of the plan
         * @param steps     list of plan steps
         */
        public CraftPlan(int totalCost, List<PlanStep> steps) {
            this.totalCost = totalCost;
            this.steps = List.copyOf(steps);
        }

        /**
         * Generates a signature string for the CraftPlan based on the sequence of recipe IDs in the steps.
         *
         * @return a string representing the signature of the plan
         */
        public String signature() {
            StringBuilder sb = new StringBuilder();
            for (PlanStep s : steps) {
                sb.append(s.recipe.id()).append("->");
            }
            return sb.toString();
        }
    }

    /**
     * Planning mode
     */
    private final List<ProcessRecipe> recipes;

    /**
     * Creates a CraftPlanner with the given list of process recipes.
     *
     * @param recipes the list of available process recipes
     */
    public CraftPlanner(List<ProcessRecipe> recipes) {
        this.recipes = List.copyOf(recipes);
    }

    /**
     * Only best plan
     *
     * @param target    desired output material
     * @param available available materials
     * @param options   planning options
     * @return optional best craft plan
     */
    public Optional<CraftPlan> planBest(MaterialMatcher target,
                                        List<MaterialMatcher> available,
                                        PlanOptions options) {
        List<CraftPlan> list = plan(target, available, Mode.BEST_ONLY, 1, options);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /**
     * X best plans
     *
     * @param target    desired output material
     * @param available available materials
     * @param k         number of plans to return
     * @param options   planning options
     * @return list of top K craft plans
     */
    public List<CraftPlan> planTopK(MaterialMatcher target,
                                    List<MaterialMatcher> available,
                                    int k,
                                    PlanOptions options) {
        if (k <= 0) throw new IllegalArgumentException("k must be > 0.");
        return plan(target, available, Mode.TOP_K, k, options);
    }

    /**
     * All possible road maps (within the limits of options.maxPlans / maxDepth)
     *
     * @param target    desired output material
     * @param available available materials
     * @param options   planning options
     * @return list of all possible craft plans
     */
    public List<CraftPlan> planAll(MaterialMatcher target,
                                   List<MaterialMatcher> available,
                                   PlanOptions options) {
        return plan(target, available, Mode.ALL, Integer.MAX_VALUE, options);
    }

    /**
     * Planning mode
     *
     * @param target  desired output material
     *                available available materials
     * @param mode    planning mode
     * @param k       number of plans to return (for TOP_K mode)
     * @param options planning options
     * @return list of craft plans
     */
    private List<CraftPlan> plan(MaterialMatcher target,
                                 List<MaterialMatcher> available,
                                 Mode mode,
                                 int k,
                                 PlanOptions options) {

        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(available, "available");
        Objects.requireNonNull(mode, "mode");
        Objects.requireNonNull(options, "options");

        // memo: targetKey -> (mode,k)-> plans
        Map<String, List<CraftPlan>> memo = new HashMap<>();
        Set<String> visiting = new HashSet<>();
        PlanBudget budget = new PlanBudget(options.maxPlans);

        List<CraftPlan> result = solve(target, available, mode, k, options, 0, memo, visiting, budget);

        // tri final
        result.sort(Comparator.comparingInt(p -> p.totalCost));

        if (mode == Mode.BEST_ONLY && !result.isEmpty()) {
            return List.of(result.get(0));
        }

        if (mode == Mode.TOP_K) {
            return result.size() <= k ? result : result.subList(0, k);
        }

        // ALL
        return result;
    }

    /**
     * Recursive solver
     *
     * @param target    desired output material
     * @param available available materials
     * @param mode      planning mode
     * @param k         number of plans to return (for TOP_K mode)
     * @param options   planning options
     * @param depth     current recursion depth
     * @param memo      memoization map
     * @param visiting  set of currently visiting target keys (for cycle detection)
     * @param budget    plan budget tracker
     * @return list of craft plans
     */
    private List<CraftPlan> solve(MaterialMatcher target,
                                  List<MaterialMatcher> available,
                                  Mode mode,
                                  int k,
                                  PlanOptions options,
                                  int depth,
                                  Map<String, List<CraftPlan>> memo,
                                  Set<String> visiting,
                                  PlanBudget budget) {

        if (budget.exhausted()) return List.of();
        if (depth > options.maxDepth) return List.of();

        // Si déjà dispo => plan vide
        if (isAvailable(target, available)) {
            return List.of(new CraftPlan(0, List.of()));
        }

        String memoKey = target.key() + "|mode=" + mode + "|k=" + (mode == Mode.TOP_K ? k : 0) + "|depth=" + depth;
        List<CraftPlan> cached = memo.get(memoKey);
        if (cached != null) return cached;

        // cycle
        if (!visiting.add(target.key())) {
            return List.of();
        }

        List<CraftPlan> allCandidates = new ArrayList<>();

        for (ProcessRecipe r : recipesThatCanProduce(target)) {
            if (budget.exhausted()) break;

            // 1) résoudre chaque input => liste de plans par input
            List<List<CraftPlan>> perInputPlans = new ArrayList<>();
            boolean ok = true;

            for (MaterialMatcher in : r.inputs()) {
                List<CraftPlan> subPlans = solve(in, available, mode, k, options, depth + 1, memo, visiting, budget);
                if (subPlans.isEmpty()) {
                    ok = false;
                    break;
                }

                // Dans TOP_K/BEST_ONLY, on limite déjà le fan-out par input
                if (mode != Mode.ALL) {
                    subPlans = trimTop(subPlans, k);
                }

                perInputPlans.add(subPlans);
            }

            if (!ok) continue;

            // 2) combiner les plans des inputs (cross product)
            List<CraftPlan> combined = combine(perInputPlans, budget);
            if (combined.isEmpty()) continue;

            // 3) ajouter l’étape de la recette
            for (CraftPlan base : combined) {
                if (budget.exhausted()) break;

                List<PlanStep> steps = new ArrayList<>(base.steps);
                steps.add(new PlanStep(r));

                CraftPlan candidate = new CraftPlan(base.totalCost + r.cost(), steps);
                allCandidates.add(candidate);
                budget.consumeOne();
            }

            // Petites optimisations
            allCandidates.sort(Comparator.comparingInt(p -> p.totalCost));

            if (mode == Mode.BEST_ONLY && !allCandidates.isEmpty()) {
                // le meilleur suffit
                allCandidates = List.of(allCandidates.get(0));
                break;
            }

            if (mode == Mode.TOP_K && allCandidates.size() > k) {
                allCandidates = new ArrayList<>(allCandidates.subList(0, k));
            }
        }

        visiting.remove(target.key());

        // Dedup
        if (options.deduplicate && allCandidates.size() > 1) {
            LinkedHashMap<String, CraftPlan> map = new LinkedHashMap<>();
            for (CraftPlan p : allCandidates) {
                map.putIfAbsent(p.signature(), p);
            }
            allCandidates = new ArrayList<>(map.values());
            allCandidates.sort(Comparator.comparingInt(p -> p.totalCost));
        }

        memo.put(memoKey, allCandidates);
        return allCandidates;
    }

    /**
     * Verify if target is available in the list of available materials.
     *
     * @param target    desired material
     * @param available list of available materials
     * @return true if target is available, false otherwise
     */
    private boolean isAvailable(MaterialMatcher target, List<MaterialMatcher> available) {
        // Simplifié:
        // - ANY dispo => tout dispo
        // - même key => dispo
        for (MaterialMatcher a : available) {
            if (a.getKind() == MaterialMatcher.Kind.ANY) return true;
            if (a.key().equals(target.key())) return true;
        }
        return false;
    }

    /**
     * Finds all recipes that can produce the target material.
     *
     * @param target desired output material
     * @return list of process recipes that can produce the target
     */
    private List<ProcessRecipe> recipesThatCanProduce(MaterialMatcher target) {
        List<ProcessRecipe> list = new ArrayList<>();
        for (ProcessRecipe r : recipes) {
            for (MaterialMatcher out : r.outputs()) {
                if (covers(out, target)) {
                    list.add(r);
                    break;
                }
            }
        }
        return list;
    }

    /**
     * "out cover target" :
     * - exact ID cover ID
     * - TYPE cover TYPE
     * - CATEGORY cover CATEGORY
     * - ANY cover All
     *
     * @param out    output material matcher from recipe
     * @param target desired target material matcher
     */
    private boolean covers(MaterialMatcher out, MaterialMatcher target) {
        if (out.getKind() == MaterialMatcher.Kind.ANY) return true;
        return out.key().equals(target.key());
    }


    /**
     * Trim the list to the top K plans based on total cost.
     *
     * @param plans list of craft plans
     * @param k     number of top plans to retain
     * @return trimmed list of craft plans
     */
    private List<CraftPlan> trimTop(List<CraftPlan> plans, int k) {
        if (plans.size() <= k) return plans;
        plans.sort(Comparator.comparingInt(p -> p.totalCost));
        return plans.subList(0, k);
    }

    /**
     * Combine one list of choices per input:
     * perInputPlans = [[p1,p2], [q1,q2,q3], [r1]]
     * => p x q x r
     *
     * @param perInputPlans list of lists of craft plans per input
     * @param budget        plan budget tracker
     * @return combined list of craft plans
     */
    private List<CraftPlan> combine(List<List<CraftPlan>> perInputPlans, PlanBudget budget) {
        if (perInputPlans.isEmpty()) return List.of(new CraftPlan(0, List.of()));

        List<CraftPlan> acc = new ArrayList<>(perInputPlans.get(0));
        for (int i = 1; i < perInputPlans.size(); i++) {
            if (budget.exhausted()) return List.of();

            List<CraftPlan> next = perInputPlans.get(i);
            List<CraftPlan> merged = new ArrayList<>();

            for (CraftPlan a : acc) {
                if (budget.exhausted()) break;
                for (CraftPlan b : next) {
                    if (budget.exhausted()) break;

                    List<PlanStep> steps = new ArrayList<>(a.steps);
                    steps.addAll(b.steps);

                    merged.add(new CraftPlan(a.totalCost + b.totalCost, steps));
                }
            }
            acc = merged;
            // petite réduction : garder les meilleurs en premier
            acc.sort(Comparator.comparingInt(p -> p.totalCost));
        }
        return acc;
    }


    /**
     * Plan Budget tracker
     */
    private static final class PlanBudget {
        /**
         * remaining plan count
         */
        private int remaining;

        /**
         * Creates a PlanBudget with the specified maximum plan count.
         */
        PlanBudget(int max) {
            this.remaining = max;
        }

        /**
         * Consumes one plan from the budget.
         */
        void consumeOne() {
            if (remaining > 0) remaining--;
        }

        /**
         * Checks if the budget is exhausted.
         */
        boolean exhausted() {
            return remaining <= 0;
        }
    }
}
