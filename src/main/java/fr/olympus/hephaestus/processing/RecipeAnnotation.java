package fr.olympus.hephaestus.processing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for processing recipes.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecipeAnnotation {

    /**
     * Unique identifier of the recipe
     *
     * @return unique identifier
     */
    String id();

    /**
     * Target factories by ID (ex: "olympus:anvil"):
     *
     * @return array of factory IDs
     */
    String[] factoryIds() default {};

    /**
     * Target factories by group (ex: "olympus:forge")
     *
     * @return array of factory group names
     */
    String[] factoryGroups() default {};

    /**
     * Optional minimum factory level required to use this recipe
     *
     * @return minimum factory level
     */
    int minFactoryLevel() default 0;
}