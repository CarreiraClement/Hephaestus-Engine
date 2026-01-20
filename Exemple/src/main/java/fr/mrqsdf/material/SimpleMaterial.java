package fr.mrqsdf.material;

import fr.olympus.hephaestus.materials.Material;
import fr.olympus.hephaestus.materials.MaterialCategory;
import fr.olympus.hephaestus.materials.MaterialType;

import java.util.List;

/**
 * A simple material with only type, categories and name.
 */
public abstract class SimpleMaterial extends Material {

    /**
     * Constructor.
     *
     * @param type       Material type.
     * @param categories Material categories.
     * @param name       Material name.
     */
    protected SimpleMaterial(MaterialType type, List<MaterialCategory> categories, String name) {
        super(type, categories, name);
    }
}
