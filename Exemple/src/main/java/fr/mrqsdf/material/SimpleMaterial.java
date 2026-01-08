package fr.mrqsdf.material;

import fr.olympus.hephaestus.materials.Material;
import fr.olympus.hephaestus.materials.MaterialCategory;
import fr.olympus.hephaestus.materials.MaterialType;

import java.util.List;

public abstract class SimpleMaterial extends Material {
    protected SimpleMaterial(MaterialType type, List<MaterialCategory> categories, String name) {
        super(type, categories, name);
    }
}
