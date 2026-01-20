package fr.mrqsdf.planner;

import java.util.List;

/**
 * Represents a plan to produce a target item.
 */
public final class Plan {
    public final String target;
    public final PlanNode root;
    public final int cost;
    public final boolean possible;

    /**
     * Constructor for Plan.
     * @param target ID of the target Material
     * @param root Root PlanNode of the plan
     * @param cost Total cost of the plan
     * @param possible Whether the plan is possible
     */
    Plan(String target, PlanNode root, int cost, boolean possible) {
        this.target = target;
        this.root = root;
        this.cost = cost;
        this.possible = possible;
    }

    /**
     * Create a plan representing an available leaf item.
     * @param id ID of the available item
     * @return Plan representing the available item
     */
    static Plan availableLeaf(String id) {
        return new Plan(id, new PlanNode(id, null, List.of()), 0, true);
    }

    /**
     * Create a plan representing an impossible target.
     * @param target ID of the impossible target
     * @return Plan representing the impossible target
     */
    static Plan impossible(String target) {
        return new Plan(target, new PlanNode(target, null, List.of()), Integer.MAX_VALUE, false);
    }

    /**
     * Generate a unique signature for the plan structure.
     * @return String signature of the plan
     */
    String signature() {
        StringBuilder sb = new StringBuilder();
        sigRec(sb, root);
        return sb.toString();
    }

    /**
     * Recursive helper to build the plan signature.
     * @param sb StringBuilder to append to
     * @param n Current PlanNode
     */
    private void sigRec(StringBuilder sb, PlanNode n) {
        sb.append(n.target).append("<-");
        sb.append(n.recipe == null ? "AVAILABLE" : n.recipe.id()).append("|");
        for (PlanNode c : n.children) sigRec(sb, c);
    }
}
