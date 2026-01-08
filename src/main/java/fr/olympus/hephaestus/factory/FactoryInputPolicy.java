package fr.olympus.hephaestus.factory;

import fr.olympus.hephaestus.materials.Material;
import fr.olympus.hephaestus.materials.MaterialCategory;
import fr.olympus.hephaestus.materials.MaterialType;

import java.util.HashSet;
import java.util.Set;

public final class FactoryInputPolicy {

    private final Set<MaterialType> allowTypes = new HashSet<>();
    private final Set<MaterialCategory> allowCategories = new HashSet<>();
    private final Set<MaterialCategory> denyCategories = new HashSet<>();

    private boolean allowAll = true;

    /** Optionnel: la factory doit avoir au moins ce level pour accepter */
    private int minFactoryLevel = Integer.MIN_VALUE;

    public FactoryInputPolicy allowAll() {
        allowAll = true;
        allowTypes.clear();
        allowCategories.clear();
        denyCategories.clear();
        minFactoryLevel = Integer.MIN_VALUE;
        return this;
    }

    public FactoryInputPolicy allowOnlyTypes(Set<? extends MaterialType> types) {
        allowAll = false;
        allowTypes.clear();
        allowTypes.addAll(types);
        return this;
    }

    public FactoryInputPolicy allowOnlyCategories(Set<? extends MaterialCategory> categories) {
        allowAll = false;
        allowCategories.clear();
        allowCategories.addAll(categories);
        return this;
    }

    public FactoryInputPolicy denyCategories(Set<? extends MaterialCategory> categories) {
        allowAll = false;
        denyCategories.clear();
        denyCategories.addAll(categories);
        return this;
    }

    public FactoryInputPolicy minFactoryLevel(int level) {
        this.minFactoryLevel = level;
        return this;
    }

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
