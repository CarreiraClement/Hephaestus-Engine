package fr.olympus.hephaestus.processing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecipeAnnotation {

    String id();

    /** Cible des variantes spécifiques */
    String[] factoryIds() default {};

    /** Cible des groupes (ex: toutes les enclumes) */
    String[] factoryGroups() default {};

    /** Optionnel: la factory doit avoir au moins ce level */
    int minFactoryLevel() default 0;
}