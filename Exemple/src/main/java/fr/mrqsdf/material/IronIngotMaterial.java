package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.IRON_INGOT;

/**
 * Iron Ingot material class.
 */
@MaterialAnnotation(id = IRON_INGOT)
public final class IronIngotMaterial extends SimpleMaterial {
    public IronIngotMaterial() {
        super(ExampleType.IRON_INGOT, List.of(ExampleCategory.METAL, ExampleCategory.IRON), "Iron Ingot");
    }
}
