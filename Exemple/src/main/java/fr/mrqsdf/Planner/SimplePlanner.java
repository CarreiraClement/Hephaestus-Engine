package fr.mrqsdf.Planner;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.ProcessRecipe;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.*;

public final class SimplePlanner {

    private final List<ProcessRecipe> recipes;
    private final HephaestusData data;

    public SimplePlanner(List<ProcessRecipe> recipes, HephaestusData data) {
        this.recipes = List.copyOf(recipes);
        this.data = data;
    }

    public Plan bestOnly(String targetMaterialId, Set<String> available, int maxDepth, int maxPlans) {
        List<Plan> all = solve(targetMaterialId, available, maxDepth, maxPlans);
        return all.isEmpty() ? Plan.impossible(targetMaterialId) : all.get(0);
    }

    public List<Plan> topK(String targetMaterialId, Set<String> available, int k, int maxDepth, int maxPlans) {
        List<Plan> all = solve(targetMaterialId, available, maxDepth, maxPlans);
        return all.size() <= k ? all : all.subList(0, k);
    }

    private List<Plan> solve(String targetId, Set<String> available, int maxDepth, int maxPlans) {
        Map<String, List<Plan>> memo = new HashMap<>();
        Set<String> visiting = new HashSet<>();
        Budget budget = new Budget(maxPlans);

        List<Plan> result = solveRec(MaterialMatcher.id(targetId), available, 0, maxDepth, memo, visiting, budget);
        result.sort(Comparator.comparingInt(p -> p.cost));

        // Dedup simple par signature
        LinkedHashMap<String, Plan> map = new LinkedHashMap<>();
        for (Plan p : result) map.putIfAbsent(p.signature(), p);
        result = new ArrayList<>(map.values());
        result.sort(Comparator.comparingInt(p -> p.cost));
        return result;
    }

    private List<Plan> solveRec(MaterialMatcher target,
                                            Set<String> available,
                                            int depth,
                                            int maxDepth,
                                            Map<String, List<Plan>> memo,
                                            Set<String> visiting,
                                            Budget budget) {
        if (budget.exhausted()) return List.of();
        if (depth > maxDepth) return List.of();

        if (target.getKind() == MaterialMatcher.Kind.ID && available.contains(target.getMaterialId())) {
            return List.of(Plan.availableLeaf(target.getMaterialId()));
        }

        String key = target.key() + "|d=" + depth;
        if (memo.containsKey(key)) return memo.get(key);

        if (!visiting.add(target.key())) return List.of();

        List<Plan> out = new ArrayList<>();

        for (ProcessRecipe r : recipesThatProduce(target)) {
            if (budget.exhausted()) break;

            List<List<Plan>> perInput = new ArrayList<>();
            boolean ok = true;

            for (MaterialMatcher in : r.inputs()) {
                List<Plan> sub = solveRec(in, available, depth + 1, maxDepth, memo, visiting, budget);
                if (sub.isEmpty()) {
                    ok = false;
                    break;
                }
                perInput.add(sub);
            }
            if (!ok) continue;

            List<List<Plan>> combos = crossProduct(perInput, budget);
            for (List<Plan> combo : combos) {
                if (budget.exhausted()) break;

                int cost = r.cost();
                List<PlanNode> children = new ArrayList<>();
                for (Plan p : combo) {
                    cost += p.cost;
                    children.add(p.root);
                }

                PlanNode root = new PlanNode(targetToId(target), r, children);
                out.add(new Plan(targetToId(target), root, cost, true));
                budget.consume();
            }
        }

        visiting.remove(target.key());
        out.sort(Comparator.comparingInt(p -> p.cost));
        memo.put(key, out);
        return out;
    }

    private String targetToId(MaterialMatcher m) {
        return (m.getKind() == MaterialMatcher.Kind.ID) ? m.getMaterialId() : m.key();
    }

    private List<ProcessRecipe> recipesThatProduce(MaterialMatcher target) {
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

    private boolean covers(MaterialMatcher out, MaterialMatcher target) {
        if (out.getKind() == MaterialMatcher.Kind.ANY) return true;
        return out.key().equals(target.key());
    }

    private List<List<Plan>> crossProduct(List<List<Plan>> lists, Budget budget) {
        if (lists.isEmpty()) return List.of(List.of());
        List<List<Plan>> acc = new ArrayList<>();
        acc.add(new ArrayList<>());

        for (List<Plan> choices : lists) {
            List<List<Plan>> next = new ArrayList<>();
            for (List<Plan> prefix : acc) {
                if (budget.exhausted()) break;
                for (Plan p : choices) {
                    if (budget.exhausted()) break;
                    List<Plan> merged = new ArrayList<>(prefix);
                    merged.add(p);
                    next.add(merged);
                }
            }
            acc = next;
        }
        return acc;
    }

    private static final class Budget {
        private int remaining;

        Budget(int max) {
            this.remaining = max;
        }

        void consume() {
            if (remaining > 0) remaining--;
        }

        boolean exhausted() {
            return remaining <= 0;
        }
    }
}
