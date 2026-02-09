package fr.mrqsdf.recipe;

import fr.olympus.hephaestus.processing.DefaultProcessRecipe;
import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.RecipeAnnotation;
import fr.olympus.hephaestus.processing.TimeWindow;

import java.util.List;

import static fr.mrqsdf.resources.Data.*;
import static fr.mrqsdf.utils.GroupsUtils.selectorIds;

/**
 * Recipe for fermenting beer in a barrel.
 */
@RecipeAnnotation(id = "ex:recipe/ferment_beer", factoryIds = {BARREL})
public final class FermentBeer extends DefaultProcessRecipe {
    public FermentBeer() {
        super(false,
                List.of(MaterialMatcher.id(WORT), MaterialMatcher.id(YEAST)),
                List.of(MaterialMatcher.id(BEER)),
                new TimeWindow(6f, 12f)
        );
    }
}
