package fr.olympus.hephaestus.register;

import java.util.Set;

/**
 * selector for recipes based on factory criteria.
 *
 * @param factoryIds      the set of factory IDs that are allowed (empty means any)
 * @param factoryGroups   the set of factory groups that are allowed (empty means any)
 * @param minFactoryLevel the minimum factory level required
 */
public record RecipeSelector(Set<String> factoryIds, Set<String> factoryGroups, int minFactoryLevel) {

    /**
     * Constructs a RecipeSelector with the specified criteria.
     *
     * @param factoryIds      the set of factory IDs that are allowed (empty means any)
     * @param factoryGroups   the set of factory groups that are allowed (empty means any)
     * @param minFactoryLevel the minimum factory level required
     */
    public RecipeSelector(Set<String> factoryIds, Set<String> factoryGroups, int minFactoryLevel) {
        if (factoryIds == null) throw new IllegalArgumentException("factoryIds cannot be null.");
        if (factoryGroups == null) throw new IllegalArgumentException("factoryGroups cannot be null.");
        this.factoryIds = Set.copyOf(factoryIds);
        this.factoryGroups = Set.copyOf(factoryGroups);
        this.minFactoryLevel = minFactoryLevel;
    }

    /**
     * Checks if a factory matches the selector criteria.
     *
     * @param factoryId               the ID of the factory to check
     * @param factoryGroupsOfInstance the groups of the factory instance
     * @param factoryLevel            the level of the factory
     * @return true if the factory matches the criteria, false otherwise
     */
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
