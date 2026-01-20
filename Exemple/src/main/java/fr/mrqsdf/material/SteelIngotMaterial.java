package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.STEEL_INGOT;

/**
 * Steel Ingot material class.
 */
@MaterialAnnotation(id = STEEL_INGOT)
public final class SteelIngotMaterial extends SimpleMaterial {
    public SteelIngotMaterial() {
        super(ExampleType.STEEL_INGOT, List.of(ExampleCategory.METAL, ExampleCategory.STEEL), "Steel Ingot");
    }
}
