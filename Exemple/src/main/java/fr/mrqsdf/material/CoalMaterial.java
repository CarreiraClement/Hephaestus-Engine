package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.COAL;

/**
 * Coal material class.
 */
@MaterialAnnotation(id = COAL)
public final class CoalMaterial extends SimpleMaterial {
    public CoalMaterial() {
        super(ExampleType.COAL, List.of(ExampleCategory.FUEL), "Coal");
    }
}
