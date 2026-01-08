package fr.olympus.hephaestus.factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FactoryAnnotation {

    /** ID unique de la variante (ex: hephaestus:anvil_copper) */
    String id();

    /** Groupes/tags (ex: hephaestus:anvil) */
    String[] groups() default {};

    /** Niveau / tier (ex: cuivre=1, fer=2) */
    int level() default 0;
}
