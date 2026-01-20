package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.CHARCOAL;

/**
 * Charcoal material class.
 */
@MaterialAnnotation(id = CHARCOAL)
public final class CharcoalMaterial extends SimpleMaterial {
    public CharcoalMaterial() {
        super(ExampleType.CHARCOAL, List.of(ExampleCategory.FUEL), "Charcoal");
    }
}
