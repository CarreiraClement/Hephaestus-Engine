package fr.mrqsdf.recipe;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.RecipeAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.*;
import static fr.mrqsdf.utils.GroupsUtils.selectorGroups;

/**
 * Recipe to make a wooden handle from an oak plank.
 */
@RecipeAnnotation(id = "ex:recipe/make_handle", factoryGroups = {GROUP_WORKBENCH})
public final class MakeHandle extends SimpleProcessRecipe {
    public MakeHandle() {
        super(
                "ex:recipe/make_handle",
                selectorGroups(GROUP_WORKBENCH),
                false,
                List.of(MaterialMatcher.id(PLANK_OAK)),
                List.of(MaterialMatcher.id(HANDLE_WOOD)),
                1,
                null
        );
    }
}
