package fr.mrqsdf.ui;

import fr.mrqsdf.planner.Plan;
import fr.mrqsdf.planner.PlanNode;
import fr.olympus.hephaestus.processing.ProcessRecipe;
import fr.olympus.hephaestus.processing.TimeWindow;
import fr.olympus.hephaestus.register.RecipeSelector;

import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Builds a CraftGraph from a production Plan.
 */
public final class CraftGraphBuilder {

    private final AtomicInteger ids = new AtomicInteger();

    /**
     * Builds a CraftGraph from the best-only Plan.
     *
     * @param plan            The production plan to convert.
     * @param availableRawIds The set of available raw material IDs.
     * @return The constructed CraftGraph.
     */
    public static CraftGraph fromBestOnlyPlan(Plan plan, Set<String> availableRawIds) {
        return new CraftGraphBuilder().build(plan, availableRawIds);
    }

    /**
     * Builds a CraftGraph from the given Plan.
     *
     * @param plan      The production plan to convert.
     * @param available The set of available raw material IDs.
     * @return The constructed CraftGraph.
     */
    public CraftGraph build(Plan plan, Set<String> available) {
        if (plan == null || plan.root == null || !plan.possible) {
            return new CraftGraph();
        }

        CraftGraph g = new CraftGraph();
        IdentityHashMap<PlanNode, MaterialNode> matNodes = new IdentityHashMap<>();

        buildRec(g, matNodes, plan.root, plan.target, available);
        return g;
    }

    /**
     * Recursive helper to build the CraftGraph.
     *
     * @param g           the CraftGraph being built
     * @param matNodes    mapping of PlanNodes to MaterialNodes
     * @param node        current PlanNode
     * @param finalTarget the final target material ID
     * @param available   set of available raw material IDs
     * @return the corresponding MaterialNode
     */
    private MaterialNode buildRec(CraftGraph g,
                                  IdentityHashMap<PlanNode, MaterialNode> matNodes,
                                  PlanNode node,
                                  String finalTarget,
                                  Set<String> available) {

        MaterialNode mn = matNodes.get(node);
        if (mn == null) {
            MaterialNode.Role role = computeRole(node, finalTarget);
            mn = new MaterialNode("M" + ids.incrementAndGet(), node.target, role);
            matNodes.put(node, mn);
            g.addNode(mn);
        }

        if (node.recipe == null) {
            return mn;
        }

        ProcessRecipe r = node.recipe;
        FactoryNode fn = new FactoryNode("F" + ids.incrementAndGet(), factoryLabel(r));
        g.addNode(fn);

        for (PlanNode child : node.children) {
            MaterialNode childMat = buildRec(g, matNodes, child, finalTarget, available);
            g.addEdge(childMat, fn);
        }
        g.addEdge(fn, mn);

        return mn;
    }

    /**
     * Computes the role of a MaterialNode based on its PlanNode and the final target.
     *
     * @param node        the PlanNode
     * @param finalTarget the final target material ID
     * @return the computed MaterialNode.Role
     */
    private MaterialNode.Role computeRole(PlanNode node, String finalTarget) {
        if (node == null) return MaterialNode.Role.INTERMEDIATE;

        if (finalTarget != null && finalTarget.equals(node.target)) {
            return MaterialNode.Role.FINAL;
        }

        if (node.recipe == null) {
            return MaterialNode.Role.RAW;
        }

        return MaterialNode.Role.INTERMEDIATE;
    }

    /**
     * Generates a label for a FactoryNode based on the ProcessRecipe.
     *
     * @param r the ProcessRecipe
     * @return the generated factory label
     */
    private String factoryLabel(ProcessRecipe r) {
        String base = pickFactoryLabel(r.selector());
        TimeWindow w = r.timeWindowOrNull();

        String tag;
        if (w != null) tag = " [AUTO " + trim(w.minSeconds()) + "-" + trim(w.maxSeconds()) + "s]";
        else if (r.ordered()) tag = " [MANUAL ordered]";
        else tag = " [MANUAL]";

        return base + tag;
    }

    /**
     * Picks a factory label based on the RecipeSelector.
     *
     * @param s the RecipeSelector
     * @return the picked factory label
     */
    private String pickFactoryLabel(RecipeSelector s) {
        if (s != null) {
            if (!s.factoryIds().isEmpty()) return "FACTORY " + s.factoryIds().iterator().next();
            if (!s.factoryGroups().isEmpty()) return "GROUP " + s.factoryGroups().iterator().next();
            if (s.minFactoryLevel() != Integer.MIN_VALUE) return "ANY FACTORY (L>=" + s.minFactoryLevel() + ")";
        }
        return "FACTORY ?";
    }

    /**
     * Trims a float to a string, removing unnecessary decimal places.
     *
     * @param f the float to trim
     * @return the trimmed string representation
     */
    private String trim(float f) {
        if (Math.abs(f - Math.round(f)) < 0.0001f) return String.valueOf(Math.round(f));
        return String.valueOf(f);
    }
}
