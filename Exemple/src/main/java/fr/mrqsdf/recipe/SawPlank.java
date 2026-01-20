package fr.mrqsdf.recipe;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.RecipeAnnotation;
import fr.olympus.hephaestus.processing.TimeWindow;

import java.util.List;

import static fr.mrqsdf.resources.Data.*;
import static fr.mrqsdf.utils.GroupsUtils.selectorGroups;

/**
 * Recipe class for sawing oak logs into oak planks.
 */
@RecipeAnnotation(id = "ex:recipe/saw_plank", factoryGroups = {GROUP_SAWMILL})
public final class SawPlank extends SimpleProcessRecipe {
    public SawPlank() {
        super(
                "ex:recipe/saw_plank",
                selectorGroups(GROUP_SAWMILL),
                false,
                List.of(MaterialMatcher.id(LOG_OAK)),
                List.of(MaterialMatcher.id(PLANK_OAK)),
                1,
                new TimeWindow(2f, 4f)
        );
    }
}
