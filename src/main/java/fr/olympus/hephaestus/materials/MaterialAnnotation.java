package fr.olympus.hephaestus.materials;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for material classes.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaterialAnnotation {

    /**
     * Unique ID of the material (e.g., "olympus:iron_ingot")
     * @return unique identifier
     */
    String id();

}
