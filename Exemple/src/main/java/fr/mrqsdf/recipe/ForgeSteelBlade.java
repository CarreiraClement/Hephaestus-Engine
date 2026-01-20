package fr.mrqsdf.recipe;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.RecipeAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.*;
import static fr.mrqsdf.utils.GroupsUtils.selectorGroupsMinLevel;

/**
 * Recipe for forging a steel blade at an anvil.
 */
@RecipeAnnotation(id = "ex:recipe/forge_steel_blade", factoryGroups = {GROUP_ANVIL}, minFactoryLevel = 2)
public final class ForgeSteelBlade extends SimpleProcessRecipe {
    public ForgeSteelBlade() {
        super(
                "ex:recipe/forge_steel_blade",
                selectorGroupsMinLevel(GROUP_ANVIL, 2),
                true,
                List.of(MaterialMatcher.id(STEEL_INGOT)),
                List.of(MaterialMatcher.id(STEEL_BLADE)),
                2,
                null
        );
    }
}
