package fr.olympus.hephaestus.planning;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.ProcessRecipe;

import java.util.*;

/**
 * Planner backward:
 * - cible un output (target)
 * - cherche toutes les recettes qui peuvent produire ce target
 * - planifie récursivement leurs inputs
 * - combine les plans des inputs (cross product) => embranchements
 */
public final class CraftPlanner {

    public enum Mode {
        BEST_ONLY,
        TOP_K,
        ALL
    }

    public static final class PlanOptions {
        public final int maxDepth;     // profondeur max de dépendances
        public final int maxPlans;     // limite globale de plans générés (surtout pour ALL)
        public final boolean deduplicate; // supprime doublons (signature steps)

        public PlanOptions(int maxDepth, int maxPlans, boolean deduplicate) {
            if (maxDepth <= 0) throw new IllegalArgumentException("maxDepth must be > 0.");
            if (maxPlans <= 0) throw new IllegalArgumentException("maxPlans must be > 0.");
            this.maxDepth = maxDepth;
            this.maxPlans = maxPlans;
            this.deduplicate = deduplicate;
        }

        public static PlanOptions safeDefaults() {
            return new PlanOptions(16, 5000, true);
        }
    }

    public static final class PlanStep {
        public final ProcessRecipe recipe;

        public PlanStep(ProcessRecipe recipe) {
            this.recipe = Objects.requireNonNull(recipe, "recipe");
        }

        @Override
        public String toString() {
            return recipe.id();
        }
    }

    public static final class CraftPlan {
        public final int totalCost;
        public final List<PlanStep> steps;

        public CraftPlan(int totalCost, List<PlanStep> steps) {
            this.totalCost = totalCost;
            this.steps = List.copyOf(steps);
        }

        public String signature() {
            StringBuilder sb = new StringBuilder();
            for (PlanStep s : steps) {
                sb.append(s.recipe.id()).append("->");
            }
            return sb.toString();
        }
    }

    private final List<ProcessRecipe> recipes;

    public CraftPlanner(List<ProcessRecipe> recipes) {
        this.recipes = List.copyOf(recipes);
    }

    /** 1 seul plan (le meilleur) */
    public Optional<CraftPlan> planBest(MaterialMatcher target,
                                        List<MaterialMatcher> available,
                                        PlanOptions options) {
        List<CraftPlan> list = plan(target, available, Mode.BEST_ONLY, 1, options);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /** X meilleurs plans */
    public List<CraftPlan> planTopK(MaterialMatcher target,
                                    List<MaterialMatcher> available,
                                    int k,
                                    PlanOptions options) {
        if (k <= 0) throw new IllegalArgumentException("k must be > 0.");
        return plan(target, available, Mode.TOP_K, k, options);
    }

    /** Toutes les routes possibles (dans les limites options.maxPlans / maxDepth) */
    public List<CraftPlan> planAll(MaterialMatcher target,
                                   List<MaterialMatcher> available,
                                   PlanOptions options) {
        return plan(target, available, Mode.ALL, Integer.MAX_VALUE, options);
    }

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
     * "out couvre target" :
     * - exact ID couvre ID
     * - TYPE couvre TYPE
     * - CATEGORY couvre CATEGORY
     * - ANY couvre tout
     *
     * (Tu pourras l’enrichir: ID couvre TYPE/CAT si tu as une DB de matériaux, etc.)
     */
    private boolean covers(MaterialMatcher out, MaterialMatcher target) {
        if (out.getKind() == MaterialMatcher.Kind.ANY) return true;
        return out.key().equals(target.key());
    }

    private List<CraftPlan> trimTop(List<CraftPlan> plans, int k) {
        if (plans.size() <= k) return plans;
        plans.sort(Comparator.comparingInt(p -> p.totalCost));
        return plans.subList(0, k);
    }

    /**
     * Combine une liste de choix par input:
     * perInputPlans = [[p1,p2], [q1,q2,q3], [r1]]
     * => p x q x r
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

    private static final class PlanBudget {
        private int remaining;
        PlanBudget(int max) { this.remaining = max; }
        void consumeOne() { if (remaining > 0) remaining--; }
        boolean exhausted() { return remaining <= 0; }
    }
}
