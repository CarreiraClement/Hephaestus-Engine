package fr.mrqsdf.planner;

import fr.olympus.hephaestus.processing.ProcessRecipe;

import java.util.List;

/**
 * Represents a node in a production plan tree.
 */
public final class PlanNode {
    public final String target;
    public final ProcessRecipe recipe; // null => available
    public final List<PlanNode> children;

    /**
     * Constructs a PlanNode with the specified target, recipe, and children.
     *
     * @param target   The target item or material for this node.
     * @param recipe   The processing recipe used to produce the target (null if available).
     * @param children The list of child PlanNodes representing sub-components or ingredients.
     */
    PlanNode(String target, ProcessRecipe recipe, List<PlanNode> children) {
        this.target = target;
        this.recipe = recipe;
        this.children = List.copyOf(children);
    }
}
