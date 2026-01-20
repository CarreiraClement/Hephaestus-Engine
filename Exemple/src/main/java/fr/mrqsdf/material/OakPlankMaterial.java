package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.PLANK_OAK;

/**
 * Oak Plank material class.
 */
@MaterialAnnotation(id = PLANK_OAK)
public final class OakPlankMaterial extends SimpleMaterial {
    public OakPlankMaterial() {
        super(ExampleType.OAK_PLANK, List.of(ExampleCategory.WOOD, ExampleCategory.PLANK), "Oak Plank");
    }
}
