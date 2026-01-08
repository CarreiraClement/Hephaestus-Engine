package fr.mrqsdf.utils;

import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.materials.Material;
import fr.olympus.hephaestus.materials.MaterialAnnotation;
import fr.olympus.hephaestus.processing.ProcessRecipe;
import fr.olympus.hephaestus.register.FactoryRegistryEntry;
import fr.olympus.hephaestus.register.ProcessRecipeRegistryEntry;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.Set;
import java.util.function.Supplier;

public class RegistryUtils {

    public static void registerMaterials(HephaestusData data, Material... defs) {
        for (Material m : defs) {
            MaterialAnnotation ann = m.getClass().getAnnotation(MaterialAnnotation.class);
            if (ann == null) throw new IllegalStateException("Missing @MaterialAnnotation on " + m.getClass().getName());
            data.registerMaterial(ann.id(), m);
        }
    }

    public static FactoryRegistryEntry entry(String id, Set<String> groups, int level, Supplier<? extends Factory> supplier) {
        return new FactoryRegistryEntry(id, groups, level, supplier);
    }

    public static void registerFactories(HephaestusData data, FactoryRegistryEntry... entries) {
        for (FactoryRegistryEntry e : entries) data.registerFactory(e);
    }

    public static void registerProcessRecipes(HephaestusData data, ProcessRecipe... recipes) {
        for (ProcessRecipe r : recipes) {
            data.registerProcessRecipe(new ProcessRecipeRegistryEntry(r.id(), r.selector(), r));
        }
    }

}
