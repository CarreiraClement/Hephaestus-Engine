package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.IRON_ORE;

/**
 * Iron Ore material class.
 */
@MaterialAnnotation(id = IRON_ORE)
public final class IronOreMaterial extends SimpleMaterial {
    public IronOreMaterial() {
        super(ExampleType.IRON_ORE, List.of(ExampleCategory.ORE, ExampleCategory.IRON), "Iron Ore");
    }
}
