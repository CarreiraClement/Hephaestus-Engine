package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.HANDLE_WOOD;

/**
 * Wood Handle material class.
 */
@MaterialAnnotation(id = HANDLE_WOOD)
public final class WoodHandleMaterial extends SimpleMaterial {
    public WoodHandleMaterial() {
        super(ExampleType.WOOD_HANDLE, List.of(ExampleCategory.WOOD, ExampleCategory.HANDLE), "Wood Handle");
    }
}
