package fr.mrqsdf.material;

import fr.mrqsdf.resources.ExampleCategory;
import fr.mrqsdf.resources.ExampleType;
import fr.olympus.hephaestus.materials.MaterialAnnotation;

import java.util.List;

import static fr.mrqsdf.resources.Data.LOG_OAK;

/**
 * Oak Log material class.
 */
@MaterialAnnotation(id = LOG_OAK)
public final class OakLogMaterial extends SimpleMaterial {
    public OakLogMaterial() {
        super(ExampleType.OAK_LOG, List.of(ExampleCategory.WOOD, ExampleCategory.LOG), "Oak Log");
    }
}
