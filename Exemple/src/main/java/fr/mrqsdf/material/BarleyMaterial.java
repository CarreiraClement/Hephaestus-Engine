package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.BARLEY;

/**
 * Barley material class.
 */
@MaterialAnnotation(id = BARLEY)
public final class BarleyMaterial extends SimpleMaterial {
    public BarleyMaterial() {
        super(ExampleType.BARLEY, List.of(ExampleCategory.FOOD, ExampleCategory.FERMENTABLE), "Barley");
    }
}
