package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.YEAST;

/**
 * Yeast material class.
 */
@MaterialAnnotation(id = YEAST)
public final class YeastMaterial extends SimpleMaterial {
    public YeastMaterial() {
        super(ExampleType.YEAST, List.of(ExampleCategory.FOOD), "Yeast");
    }
}
