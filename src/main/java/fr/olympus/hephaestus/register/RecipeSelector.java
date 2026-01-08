package fr.olympus.hephaestus.register;

import java.util.Set;

public record RecipeSelector(Set<String> factoryIds, Set<String> factoryGroups, int minFactoryLevel) {

    public RecipeSelector(Set<String> factoryIds, Set<String> factoryGroups, int minFactoryLevel) {
        if (factoryIds == null) throw new IllegalArgumentException("factoryIds cannot be null.");
        if (factoryGroups == null) throw new IllegalArgumentException("factoryGroups cannot be null.");
        this.factoryIds = Set.copyOf(factoryIds);
        this.factoryGroups = Set.copyOf(factoryGroups);
        this.minFactoryLevel = minFactoryLevel;
    }

    public boolean matchesFactory(String factoryId, Set<String> factoryGroupsOfInstance, int factoryLevel) {
        if (factoryLevel < minFactoryLevel) return false;

        boolean idMatch = factoryIds.isEmpty() || factoryIds.contains(factoryId);

        boolean groupMatch;
        if (factoryGroups.isEmpty()) {
            groupMatch = true; // pas de contrainte de groupe
        } else {
            groupMatch = false;
            for (String g : factoryGroupsOfInstance) {
                if (factoryGroups.contains(g)) {
                    groupMatch = true;
                    break;
                }
            }
        }

        return idMatch && groupMatch;
    }
}
