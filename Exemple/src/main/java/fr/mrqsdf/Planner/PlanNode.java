package fr.mrqsdf.Planner;

import fr.olympus.hephaestus.processing.ProcessRecipe;

import java.util.List;

public final class PlanNode {
    public final String target;
    public final ProcessRecipe recipe; // null => dispo
    public final List<PlanNode> children;

    PlanNode(String target, ProcessRecipe recipe, List<PlanNode> children) {
        this.target = target;
        this.recipe = recipe;
        this.children = List.copyOf(children);
    }
}
