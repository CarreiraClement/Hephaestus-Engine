package fr.olympus.hephaestus.materials;

import java.util.List;
public abstract class Material {

    protected final MaterialType type;
    protected final List<MaterialCategory> categories;
    protected final String name;

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

    public MaterialType getType() {
        return type;
    }

    public List<MaterialCategory> getCategories() {
        return categories;
    }

    public String getName() {
        return name;
    }

}
