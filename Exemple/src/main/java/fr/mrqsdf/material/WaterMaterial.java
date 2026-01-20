package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.WATER;

/**
 * Water material class.
 */
@MaterialAnnotation(id = WATER)
public final class WaterMaterial extends SimpleMaterial {
    public WaterMaterial() {
        super(ExampleType.WATER, List.of(ExampleCategory.LIQUID), "Water");
    }
}
