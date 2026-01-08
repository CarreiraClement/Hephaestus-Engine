package fr.mrqsdf.utils;

import fr.olympus.hephaestus.register.RecipeSelector;

import java.util.Set;

public class GroupsUtils {

    public static RecipeSelector selectorGroups(String... groups) {
        return new RecipeSelector(Set.of(), Set.of(groups), Integer.MIN_VALUE);
    }

    public static RecipeSelector selectorIds(String... ids) {
        return new RecipeSelector(Set.of(ids), Set.of(), Integer.MIN_VALUE);
    }

    public static RecipeSelector selectorGroupsMinLevel(String group, int minLevel) {
        return new RecipeSelector(Set.of(), Set.of(group), minLevel);
    }

}
