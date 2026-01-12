package fr.olympus.hephaestus.register;

import fr.olympus.hephaestus.processing.ProcessRecipe;

/**
 * An entry in the process recipe registry, associating an ID and a selector with a process recipe.
 *
 * @param id       The unique identifier for the registry entry.
 * @param selector The recipe selector used to match recipes.
 * @param recipe   The process recipe associated with this entry.
 */
public record ProcessRecipeRegistryEntry(String id, RecipeSelector selector, ProcessRecipe recipe) {

    /**
     * Constructs a ProcessRecipeRegistryEntry with the specified id, selector, and recipe.
     *
     * @param id       The unique identifier for the registry entry.
     * @param selector The recipe selector used to match recipes.
     * @param recipe   The process recipe associated with this entry.
     * @throws IllegalArgumentException if any argument is null or if id is blank.
     */
    public ProcessRecipeRegistryEntry {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be null/blank.");
        if (selector == null) throw new IllegalArgumentException("selector cannot be null.");
        if (recipe == null) throw new IllegalArgumentException("recipe cannot be null.");
    }
}
