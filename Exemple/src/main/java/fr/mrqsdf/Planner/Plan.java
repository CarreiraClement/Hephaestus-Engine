package fr.mrqsdf.Planner;

import java.util.List;

public final class Plan {
    public final String target;
    public final PlanNode root;
    public final int cost;
    public final boolean possible;

    Plan(String target, PlanNode root, int cost, boolean possible) {
        this.target = target;
        this.root = root;
        this.cost = cost;
        this.possible = possible;
    }

    static Plan availableLeaf(String id) {
        return new Plan(id, new PlanNode(id, null, List.of()), 0, true);
    }

    static Plan impossible(String target) {
        return new Plan(target, new PlanNode(target, null, List.of()), Integer.MAX_VALUE, false);
    }

    String signature() {
        StringBuilder sb = new StringBuilder();
        sigRec(sb, root);
        return sb.toString();
    }

    private void sigRec(StringBuilder sb, PlanNode n) {
        sb.append(n.target).append("<-");
        sb.append(n.recipe == null ? "AVAILABLE" : n.recipe.id()).append("|");
        for (PlanNode c : n.children) sigRec(sb, c);
    }
}
