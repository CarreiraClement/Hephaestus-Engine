package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.STEEL_SWORD;

/**
 * Steel Sword material class.
 */
@MaterialAnnotation(id = STEEL_SWORD)
public final class SteelSwordMaterial extends SimpleMaterial {
    public SteelSwordMaterial() {
        super(ExampleType.STEEL_SWORD, List.of(ExampleCategory.METAL, ExampleCategory.STEEL, ExampleCategory.SWORD), "Steel Sword");
    }
}
