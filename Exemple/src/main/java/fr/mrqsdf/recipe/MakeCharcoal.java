package fr.mrqsdf.recipe;

import fr.olympus.hephaestus.processing.DefaultProcessRecipe;
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
public final class MakeCharcoal extends DefaultProcessRecipe {
    public MakeCharcoal() {
        super(false,
                List.of(MaterialMatcher.id(LOG_OAK)),
                List.of(MaterialMatcher.id(CHARCOAL)),
                new TimeWindow(5f, 10f)
        );
    }
}
