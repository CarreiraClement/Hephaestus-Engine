package fr.olympus.hephaestus.planning;

import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.*;

/**
 * Facade for the CraftPlanner, providing simplified methods to obtain crafting plans.
 */
public final class PlannerFacade {

    /**
     * The underlying CraftPlanner instance used for planning.
     */
    private final CraftPlanner planner;
    /**
     * The HephaestusData instance used for material data expansion.
     */
    private final HephaestusData data;

    /**
     * Constructs a PlannerFacade with the specified CraftPlanner and HephaestusData.
     *
     * @param planner The CraftPlanner instance to use for planning.
     */
    public PlannerFacade(CraftPlanner planner) {
        this.planner = Objects.requireNonNull(planner, "planner");
        this.data = Objects.requireNonNull(Hephaestus.getData(), "data");
    }

    /**
     * Finds the best crafting plan for the given target material.
     *
     * @param target      The target material to craft.
     * @param available   The list of available materials.
     * @param opt         The planning options.
     * @param expandLimit The limit for material expansion.
     * @return An Optional containing the best CraftPlan if found, otherwise empty.
     */
    public Optional<CraftPlanner.CraftPlan> bestOnly(MaterialMatcher target,
                                                     List<MaterialMatcher> available,
                                                     CraftPlanner.PlanOptions opt,
                                                     int expandLimit) {
        List<CraftPlanner.CraftPlan> plans = allInternal(target, available, opt, expandLimit, Mode.BEST_ONLY, 1);
        return plans.isEmpty() ? Optional.empty() : Optional.of(plans.get(0));
    }

    /**
     * Finds the top K crafting plans for the given target material.
     *
     * @param target      The target material to craft.
     * @param available   The list of available materials.
     * @param k           The number of top plans to retrieve.
     * @param opt         The planning options.
     * @param expandLimit The limit for material expansion.
     * @return A list of the top K CraftPlans.
     */
    public List<CraftPlanner.CraftPlan> topK(MaterialMatcher target,
                                             List<MaterialMatcher> available,
                                             int k,
                                             CraftPlanner.PlanOptions opt,
                                             int expandLimit) {
        return allInternal(target, available, opt, expandLimit, Mode.TOP_K, k);
    }

    /**
     * Finds all possible crafting plans for the given target material.
     *
     * @param target      The target material to craft.
     * @param available   The list of available materials.
     * @param opt         The planning options.
     * @param expandLimit The limit for material expansion.
     * @return A list of all possible CraftPlans.
     */
    public List<CraftPlanner.CraftPlan> allRoutes(MaterialMatcher target,
                                                  List<MaterialMatcher> available,
                                                  CraftPlanner.PlanOptions opt,
                                                  int expandLimit) {
        return allInternal(target, available, opt, expandLimit, Mode.ALL, Integer.MAX_VALUE);
    }

    /**
     * Internal method to handle different planning modes.
     *
     * @param target      The target material to craft.
     * @param available   The list of available materials.
     * @param opt         The planning options.
     * @param expandLimit The limit for material expansion.
     * @param mode        The mode of planning (BEST_ONLY, TOP_K, ALL).
     * @param k           The number of top plans to retrieve (used in TOP_K mode).
     * @return A list of CraftPlans based on the specified mode.
     */
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
        out.sort(Comparator.comparingInt(p -> p.totalCost()));

        if (mode == Mode.BEST_ONLY) {
            return out.isEmpty() ? List.of() : List.of(out.getFirst());
        }
        if (mode == Mode.TOP_K && out.size() > k) {
            return out.subList(0, k);
        }
        return out;
    }
}
