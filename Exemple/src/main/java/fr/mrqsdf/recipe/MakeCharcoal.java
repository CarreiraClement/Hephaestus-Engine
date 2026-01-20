package fr.mrqsdf.recipe;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.RecipeAnnotation;
import fr.olympus.hephaestus.processing.TimeWindow;

import java.util.List;

import static fr.mrqsdf.resources.Data.*;
import static fr.mrqsdf.utils.GroupsUtils.selectorGroups;

/**
 * Recipe to make charcoal from oak logs.
 */
@RecipeAnnotation(id = "ex:recipe/make_charcoal", factoryGroups = {GROUP_CHARCOAL})
public final class MakeCharcoal extends SimpleProcessRecipe {
    public MakeCharcoal() {
        super(
                "ex:recipe/make_charcoal",
                selectorGroups(GROUP_CHARCOAL),
                false,
                List.of(MaterialMatcher.id(LOG_OAK)),
                List.of(MaterialMatcher.id(CHARCOAL)),
                2,
                new TimeWindow(5f, 10f)
        );
    }
}
