package fr.olympus.hephaestus.materials;

import java.util.List;

/**
 * Abstract representation of a material with type, categories, and name.
 */
public abstract class Material {

    /**
     * The type of the material.
     */
    protected final MaterialType type;
    /**
     * The categories associated with the material.
     */
    protected final List<MaterialCategory> categories;
    /**
     * The name of the material.
     */
    protected final String name;

    /**
     * Constructs a Material with the specified type, categories, and name.
     *
     * @param type       the type of the material
     * @param categories the categories associated with the material
     * @param name       the name of the material
     * @throws IllegalArgumentException if any argument is invalid
     */
    protected Material(MaterialType type, List<MaterialCategory> categories, String name) {
        if (!(type instanceof Enum<?>)) {
            throw new IllegalArgumentException("Material type must be a non-null enum. :" + type);
        }
        if (categories == null) {
            throw new IllegalArgumentException("Material categories cannot be null.");
        }
        categories.forEach(category -> {
            if (!(category instanceof Enum<?>)) {
                throw new IllegalArgumentException("Each material category must be a non-null enum. :" + category);
            }
        });
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Material name cannot be null or empty.");
        }
        this.type = type;
        this.categories = categories;
        this.name = name;
    }

    /**
     * Returns the type of the material.
     *
     * @return the material type
     */
    public MaterialType getType() {
        return type;
    }

    /**
     * Returns the categories associated with the material.
     *
     * @return list of material categories
     */
    public List<MaterialCategory> getCategories() {
        return categories;
    }

    /**
     * Returns the name of the material.
     *
     * @return the material name
     */
    public String getName() {
        return name;
    }

}
