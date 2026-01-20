package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.STEEL_BLADE;

/**
 * Steel Blade material class.
 */
@MaterialAnnotation(id = STEEL_BLADE)
public final class SteelBladeMaterial extends SimpleMaterial {
    public SteelBladeMaterial() {
        super(ExampleType.STEEL_BLADE, List.of(ExampleCategory.METAL, ExampleCategory.STEEL, ExampleCategory.BLADE), "Steel Blade");
    }
}
