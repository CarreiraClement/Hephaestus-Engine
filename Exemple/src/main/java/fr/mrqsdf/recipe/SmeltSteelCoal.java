package fr.mrqsdf.recipe;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.RecipeAnnotation;
import fr.olympus.hephaestus.processing.TimeWindow;

import java.util.List;

import static fr.mrqsdf.resources.Data.*;
import static fr.mrqsdf.utils.GroupsUtils.selectorIds;

/**
 * Recipe to smelt steel using iron ingot and coal in a blast furnace.
 */
@RecipeAnnotation(id = "ex:recipe/smelt_steel_coal", factoryIds = {FURNACE_BLAST})
public final class SmeltSteelCoal extends SimpleProcessRecipe {
    public SmeltSteelCoal() {
        super(
                "ex:recipe/smelt_steel_coal",
                selectorIds(FURNACE_BLAST),
                false,
                List.of(MaterialMatcher.id(IRON_INGOT), MaterialMatcher.id(COAL)),
                List.of(MaterialMatcher.id(STEEL_INGOT)),
                5,
                new TimeWindow(10f, 20f)
        );
    }
}
