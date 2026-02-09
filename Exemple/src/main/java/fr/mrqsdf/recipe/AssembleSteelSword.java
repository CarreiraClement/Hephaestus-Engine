package fr.mrqsdf.recipe;

import fr.olympus.hephaestus.processing.DefaultProcessRecipe;
import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.RecipeAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.*;
import static fr.mrqsdf.utils.GroupsUtils.selectorGroupsMinLevel;

/**
 * Recipe to assemble a steel sword from a steel blade and a wooden handle.
 */
@RecipeAnnotation(id = "ex:recipe/assemble_steel_sword", factoryGroups = {GROUP_ANVIL}, minFactoryLevel = 2)
public final class AssembleSteelSword extends DefaultProcessRecipe {
    /**
     * Constructor for AssembleSteelSword recipe.
     */
    public AssembleSteelSword() {
        super(false,
                List.of(MaterialMatcher.id(STEEL_BLADE), MaterialMatcher.id(HANDLE_WOOD)),
                List.of(MaterialMatcher.id(STEEL_SWORD)),
                null
        );
    }
}
