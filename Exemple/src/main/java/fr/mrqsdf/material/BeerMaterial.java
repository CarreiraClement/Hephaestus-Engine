package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.BEER;

/**
 * Beer material class.
 */
@MaterialAnnotation(id = BEER)
public final class BeerMaterial extends SimpleMaterial {
    public BeerMaterial() {
        super(ExampleType.BEER, List.of(ExampleCategory.LIQUID, ExampleCategory.ALCOHOL), "Beer");
    }
}
