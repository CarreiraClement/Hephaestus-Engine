package fr.mrqsdf.ui;

import fr.mrqsdf.Planner.Plan;
import fr.mrqsdf.Planner.PlanNode;
import fr.olympus.hephaestus.processing.ProcessRecipe;
import fr.olympus.hephaestus.processing.TimeWindow;
import fr.olympus.hephaestus.register.RecipeSelector;

import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class CraftGraphBuilder {

    private final AtomicInteger ids = new AtomicInteger();

    /** Méthode utilisée par ton viewer. */
    public static CraftGraph fromBestOnlyPlan(Plan plan, Set<String> availableRawIds) {
        return new CraftGraphBuilder().build(plan, availableRawIds);
    }

    public CraftGraph build(Plan plan, Set<String> available) {
        if (plan == null || plan.root == null || !plan.possible) {
            return new CraftGraph();
        }

        CraftGraph g = new CraftGraph();
        IdentityHashMap<PlanNode, MaterialNode> matNodes = new IdentityHashMap<>();

        buildRec(g, matNodes, plan.root, plan.target, available);
        return g;
    }

    private MaterialNode buildRec(CraftGraph g,
                                  IdentityHashMap<PlanNode, MaterialNode> matNodes,
                                  PlanNode node,
                                  String finalTarget,
                                  Set<String> available) {

        // 1) Node matériau (unique par PlanNode)
        MaterialNode mn = matNodes.get(node);
        if (mn == null) {
            MaterialNode.Role role = computeRole(node, finalTarget);
            mn = new MaterialNode("M" + ids.incrementAndGet(), node.target, role);
            matNodes.put(node, mn);
            g.addNode(mn);
        }

        // 2) Leaf : rien d'autre
        if (node.recipe == null) {
            return mn;
        }

        // 3) Factory node + edges childMat -> factory -> mn
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

    private MaterialNode.Role computeRole(PlanNode node, String finalTarget) {
        if (node == null) return MaterialNode.Role.INTERMEDIATE;

        // FINAL si c'est le target final
        if (finalTarget != null && finalTarget.equals(node.target)) {
            return MaterialNode.Role.FINAL;
        }

        // RAW si leaf (matière première de base), même si pas “available”
        if (node.recipe == null) {
            return MaterialNode.Role.RAW;
        }

        return MaterialNode.Role.INTERMEDIATE;
    }

    private String factoryLabel(ProcessRecipe r) {
        String base = pickFactoryLabel(r.selector());
        TimeWindow w = r.timeWindowOrNull();

        String tag;
        if (w != null) tag = " [AUTO " + trim(w.minSeconds()) + "-" + trim(w.maxSeconds()) + "s]";
        else if (r.ordered()) tag = " [MANUAL ordered]";
        else tag = " [MANUAL]";

        return base + tag;
    }

    private String pickFactoryLabel(RecipeSelector s) {
        if (s != null) {
            if (!s.factoryIds().isEmpty()) return "FACTORY " + s.factoryIds().iterator().next();
            if (!s.factoryGroups().isEmpty()) return "GROUP " + s.factoryGroups().iterator().next();
            if (s.minFactoryLevel() != Integer.MIN_VALUE) return "ANY FACTORY (L>=" + s.minFactoryLevel() + ")";
        }
        return "FACTORY ?";
    }

    private String trim(float f) {
        if (Math.abs(f - Math.round(f)) < 0.0001f) return String.valueOf(Math.round(f));
        return String.valueOf(f);
    }
}
