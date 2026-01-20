package fr.mrqsdf.recipe;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.RecipeAnnotation;
import fr.olympus.hephaestus.processing.TimeWindow;

import java.util.List;

import static fr.mrqsdf.resources.Data.*;
import static fr.mrqsdf.utils.GroupsUtils.selectorGroupsMinLevel;

/**
 * Recipe to smelt iron ore into iron ingots using coal as fuel.
 */
@RecipeAnnotation(id = "ex:recipe/smelt_iron", factoryGroups = {GROUP_FURNACE}, minFactoryLevel = 1)
public final class SmeltIron extends SimpleProcessRecipe {
    public SmeltIron() {
        super(
                "ex:recipe/smelt_iron",
                selectorGroupsMinLevel(GROUP_FURNACE, 1),
                false,
                List.of(MaterialMatcher.id(IRON_ORE), MaterialMatcher.id(COAL)),
                List.of(MaterialMatcher.id(IRON_INGOT)),
                3,
                new TimeWindow(8f, 15f)
        );
    }
}
