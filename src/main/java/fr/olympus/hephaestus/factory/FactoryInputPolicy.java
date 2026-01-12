package fr.olympus.hephaestus.factory;

import fr.olympus.hephaestus.materials.Material;
import fr.olympus.hephaestus.materials.MaterialCategory;
import fr.olympus.hephaestus.materials.MaterialType;

import java.util.HashSet;
import java.util.Set;

/**
 * Policy defining what materials a factory can accept as input.
 */
public final class FactoryInputPolicy {

    /**
     * Allowed material types
     */
    private final Set<MaterialType> allowTypes = new HashSet<>();

    /**
     * Allowed material categories
     */
    private final Set<MaterialCategory> allowCategories = new HashSet<>();

    /**
     * Denied material categories
     */
    private final Set<MaterialCategory> denyCategories = new HashSet<>();

    /**
     * If true, all materials are allowed unless specifically denied
     */
    private boolean allowAll = true;

    /**
     * Optional: the factory must have at least this level to accept
     * Defaults to Integer.MIN_VALUE (no restriction)
     */
    private int minFactoryLevel = Integer.MIN_VALUE;

    /**
     * Constructs a new FactoryInputPolicy with default settings (allow all).
     */
    public FactoryInputPolicy() {
    }

    /**
     * Creates a new FactoryInputPolicy with default settings (allow all).
     * @return a new FactoryInputPolicy
     */
    public FactoryInputPolicy allowAll() {
        allowAll = true;
        allowTypes.clear();
        allowCategories.clear();
        denyCategories.clear();
        minFactoryLevel = Integer.MIN_VALUE;
        return this;
    }

    /**
     * Configures the policy to allow only the specified material types.
     *
     * @param types the set of material types to allow
     * @return the updated FactoryInputPolicy
     */
    public FactoryInputPolicy allowOnlyTypes(Set<? extends MaterialType> types) {
        allowAll = false;
        allowTypes.clear();
        allowTypes.addAll(types);
        return this;
    }

    /**
     * Configures the policy to allow only the specified material categories.
     *
     * @param categories the set of material categories to allow
     * @return the updated FactoryInputPolicy
     */
    public FactoryInputPolicy allowOnlyCategories(Set<? extends MaterialCategory> categories) {
        allowAll = false;
        allowCategories.clear();
        allowCategories.addAll(categories);
        return this;
    }

    /**
     * Configures the policy to deny the specified material categories.
     *
     * @param categories the set of material categories to deny
     * @return the updated FactoryInputPolicy
     */
    public FactoryInputPolicy denyCategories(Set<? extends MaterialCategory> categories) {
        allowAll = false;
        denyCategories.clear();
        denyCategories.addAll(categories);
        return this;
    }

    /**
     * Sets the minimum factory level required to accept materials.
     *
     * @param level the minimum factory level
     * @return the updated FactoryInputPolicy
     */
    public FactoryInputPolicy minFactoryLevel(int level) {
        this.minFactoryLevel = level;
        return this;
    }

    /**
     * Checks if a material can be inserted into the factory based on the policy.
     *
     * @param materialDef  the material definition to check
     * @param factoryLevel the level of the factory
     * @return true if the material can be inserted, false otherwise
     */
    public boolean canInsert(Material materialDef, int factoryLevel) {
        if (materialDef == null) return false;
        if (factoryLevel < minFactoryLevel) return false;
        if (allowAll) return true;

        // deny d'abord
        for (MaterialCategory c : materialDef.getCategories()) {
            if (denyCategories.contains(c)) return false;
        }

        boolean typeOk = allowTypes.isEmpty() || allowTypes.contains(materialDef.getType());

        boolean catOk = allowCategories.isEmpty();
        if (!allowCategories.isEmpty()) {
            catOk = false;
            for (MaterialCategory c : materialDef.getCategories()) {
                if (allowCategories.contains(c)) {
                    catOk = true;
                    break;
                }
            }
        }

        return typeOk && catOk;
    }
}
