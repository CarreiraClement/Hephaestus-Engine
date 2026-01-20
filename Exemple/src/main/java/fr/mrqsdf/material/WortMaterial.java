package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.WORT;

/**
 * Wort material class.
 */
@MaterialAnnotation(id = WORT)
public final class WortMaterial extends SimpleMaterial {
    public WortMaterial() {
        super(ExampleType.WORT, List.of(ExampleCategory.LIQUID, ExampleCategory.FERMENTABLE), "Wort");
    }
}
