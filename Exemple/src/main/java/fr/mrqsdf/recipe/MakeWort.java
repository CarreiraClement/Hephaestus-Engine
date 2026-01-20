package fr.mrqsdf.recipe;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.RecipeAnnotation;
import fr.olympus.hephaestus.processing.TimeWindow;

import java.util.List;

import static fr.mrqsdf.resources.Data.*;
import static fr.mrqsdf.utils.GroupsUtils.selectorIds;

/**
 * Recipe to make Wort from Water and Barley in a Barrel.
 */
@RecipeAnnotation(id = "ex:recipe/make_wort", factoryIds = {BARREL})
public final class MakeWort extends SimpleProcessRecipe {
    public MakeWort() {
        super(
                "ex:recipe/make_wort",
                selectorIds(BARREL),
                false,
                List.of(MaterialMatcher.id(WATER), MaterialMatcher.id(BARLEY)),
                List.of(MaterialMatcher.id(WORT)),
                2,
                new TimeWindow(4f, 8f)
        );
    }
}
