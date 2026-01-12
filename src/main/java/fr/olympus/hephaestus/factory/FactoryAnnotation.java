package fr.olympus.hephaestus.factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for factory classes.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FactoryAnnotation {

    /**
     * Unique ID of the variant (ex: hephaestus:anvil_copper)
     * @return unique identifier
     */
    String id();

    /**
     * Groupes/tags (ex: hephaestus:anvil)
     * @return array of group names
     */
    String[] groups() default {};

    /**
     * Level / tier (ex: copper=1, iron=2)
     * @return level integer
     */
    int level() default 0;
}
