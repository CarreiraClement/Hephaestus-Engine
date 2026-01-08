package fr.olympus.hephaestus.planning;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.*;

public final class PlannerFacade {

    private final CraftPlanner planner;
    private final HephaestusData data;

    public PlannerFacade(CraftPlanner planner, HephaestusData data) {
        this.planner = Objects.requireNonNull(planner, "planner");
        this.data = Objects.requireNonNull(data, "data");
    }

    public Optional<CraftPlanner.CraftPlan> bestOnly(MaterialMatcher target,
                                                     List<MaterialMatcher> available,
                                                     CraftPlanner.PlanOptions opt,
                                                     int expandLimit) {
        List<CraftPlanner.CraftPlan> plans = allInternal(target, available, opt, expandLimit, Mode.BEST_ONLY, 1);
        return plans.isEmpty() ? Optional.empty() : Optional.of(plans.get(0));
    }

    public List<CraftPlanner.CraftPlan> topK(MaterialMatcher target,
                                             List<MaterialMatcher> available,
                                             int k,
                                             CraftPlanner.PlanOptions opt,
                                             int expandLimit) {
        return allInternal(target, available, opt, expandLimit, Mode.TOP_K, k);
    }

    public List<CraftPlanner.CraftPlan> allRoutes(MaterialMatcher target,
                                                  List<MaterialMatcher> available,
                                                  CraftPlanner.PlanOptions opt,
                                                  int expandLimit) {
        return allInternal(target, available, opt, expandLimit, Mode.ALL, Integer.MAX_VALUE);
    }

    private enum Mode { BEST_ONLY, TOP_K, ALL }

    private List<CraftPlanner.CraftPlan> allInternal(MaterialMatcher target,
                                                     List<MaterialMatcher> available,
                                                     CraftPlanner.PlanOptions opt,
                                                     int expandLimit,
                                                     Mode mode,
                                                     int k) {

        List<MaterialMatcher> concreteTargets = MaterialTargetExpander.expandToConcreteIds(target, data, expandLimit);

        List<CraftPlanner.CraftPlan> all = new ArrayList<>();
        for (MaterialMatcher t : concreteTargets) {
            if (t.getKind() == MaterialMatcher.Kind.ANY) {
                // demander "ANY" n'a pas de sens comme objectif final => on ignore ou on renvoie vide
                continue;
            }

            switch (mode) {
                case BEST_ONLY -> planner.planBest(t, available, opt).ifPresent(all::add);
                case TOP_K -> all.addAll(planner.planTopK(t, available, k, opt));
                case ALL -> all.addAll(planner.planAll(t, available, opt));
            }
        }

        // Dedup + tri
        LinkedHashMap<String, CraftPlanner.CraftPlan> map = new LinkedHashMap<>();
        for (CraftPlanner.CraftPlan p : all) {
            map.putIfAbsent(p.signature(), p);
        }
        List<CraftPlanner.CraftPlan> out = new ArrayList<>(map.values());
        out.sort(Comparator.comparingInt(p -> p.totalCost));

        if (mode == Mode.BEST_ONLY) {
            return out.isEmpty() ? List.of() : List.of(out.getFirst());
        }
        if (mode == Mode.TOP_K && out.size() > k) {
            return out.subList(0, k);
        }
        return out;
    }
}
