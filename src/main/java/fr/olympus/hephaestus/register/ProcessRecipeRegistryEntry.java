package fr.olympus.hephaestus.register;

import fr.olympus.hephaestus.processing.ProcessRecipe;

public record ProcessRecipeRegistryEntry(String id, RecipeSelector selector, ProcessRecipe recipe) {

    public ProcessRecipeRegistryEntry {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be null/blank.");
        if (selector == null) throw new IllegalArgumentException("selector cannot be null.");
        if (recipe == null) throw new IllegalArgumentException("recipe cannot be null.");
    }
}
