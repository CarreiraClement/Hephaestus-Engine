package fr.mrqsdf.recipe;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.RecipeAnnotation;
import fr.olympus.hephaestus.processing.TimeWindow;

import java.util.List;

import static fr.mrqsdf.resources.Data.*;
import static fr.mrqsdf.utils.GroupsUtils.selectorIds;

// Route 2: acier = iron_ingot + charcoal (blast furnace) => nécessite MakeCharcoal
@RecipeAnnotation(id = "ex:recipe/smelt_steel_charcoal", factoryIds = {FURNACE_BLAST})
public final class SmeltSteelCharcoal extends SimpleProcessRecipe {
    public SmeltSteelCharcoal() {
        super(
                "ex:recipe/smelt_steel_charcoal",
                selectorIds(FURNACE_BLAST),
                false,
                List.of(MaterialMatcher.id(IRON_INGOT), MaterialMatcher.id(CHARCOAL)),
                List.of(MaterialMatcher.id(STEEL_INGOT)),
                6,
                new TimeWindow(10f, 20f)
        );
    }
}
