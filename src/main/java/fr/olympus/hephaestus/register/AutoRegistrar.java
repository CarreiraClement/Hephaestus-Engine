package fr.olympus.hephaestus.register;

import fr.olympus.hephaestus.Hephaestus;
import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.factory.FactoryAnnotation;
import fr.olympus.hephaestus.materials.Material;
import fr.olympus.hephaestus.materials.MaterialAnnotation;
import fr.olympus.hephaestus.processing.ProcessRecipe;
import fr.olympus.hephaestus.processing.RecipeAnnotation;
import fr.olympus.hephaestus.resources.HephaestusData;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class AutoRegistrar {

    private AutoRegistrar() {}

    public static void register(RegisterType type, String... basePackages) {
        if (type == null) throw new IllegalArgumentException("type cannot be null.");
        if (basePackages == null || basePackages.length == 0) throw new IllegalArgumentException("basePackages required.");

        HephaestusData data = Hephaestus.getData();

        try (ScanResult scan = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(basePackages)
                .scan()) {

            if (type == RegisterType.ALL || type == RegisterType.MATERIAL) {
                for (ClassInfo ci : scan.getClassesWithAnnotation(MaterialAnnotation.class.getName())) {
                    Class<?> raw = ci.loadClass();
                    if (!Material.class.isAssignableFrom(raw)) {
                        throw new IllegalStateException("@MaterialAnnotation on non-Material: " + raw.getName());
                    }
                    @SuppressWarnings("unchecked")
                    Class<? extends Material> clazz = (Class<? extends Material>) raw;

                    MaterialAnnotation ann = clazz.getAnnotation(MaterialAnnotation.class);
                    data.registerMaterial(ann.id(), newInstance(clazz));
                }
            }

            if (type == RegisterType.ALL || type == RegisterType.FACTORY) {
                for (ClassInfo ci : scan.getClassesWithAnnotation(FactoryAnnotation.class.getName())) {
                    Class<?> raw = ci.loadClass();
                    if (!Factory.class.isAssignableFrom(raw)) {
                        throw new IllegalStateException("@FactoryAnnotation on non-Factory: " + raw.getName());
                    }
                    @SuppressWarnings("unchecked")
                    Class<? extends Factory> clazz = (Class<? extends Factory>) raw;

                    FactoryAnnotation ann = clazz.getAnnotation(FactoryAnnotation.class);

                    Set<String> groups = new HashSet<>(Arrays.asList(ann.groups()));
                    FactoryRegistryEntry entry = new FactoryRegistryEntry(
                            ann.id(),
                            groups,
                            ann.level(),
                            () -> newInstance(clazz)
                    );

                    data.registerFactory(entry);
                }
            }

            if (type == RegisterType.ALL || type == RegisterType.RECIPE) {
                for (ClassInfo ci : scan.getClassesWithAnnotation(RecipeAnnotation.class.getName())) {
                    Class<?> raw = ci.loadClass();
                    if (!ProcessRecipe.class.isAssignableFrom(raw)) {
                        throw new IllegalStateException("@RecipeAnnotation on non-ProcessRecipe: " + raw.getName());
                    }
                    @SuppressWarnings("unchecked")
                    Class<? extends ProcessRecipe> clazz = (Class<? extends ProcessRecipe>) raw;

                    RecipeAnnotation ann = clazz.getAnnotation(RecipeAnnotation.class);

                    RecipeSelector selector = new RecipeSelector(
                            Set.of(ann.factoryIds()),
                            Set.of(ann.factoryGroups()),
                            ann.minFactoryLevel()
                    );

                    ProcessRecipeRegistryEntry entry = new ProcessRecipeRegistryEntry(
                            ann.id(),
                            selector,
                            newInstance(clazz)
                    );

                    data.registerProcessRecipe(entry);
                }
            }
        }
    }

    private static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<T> c = clazz.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("No-arg constructor required for auto-register: " + clazz.getName(), e);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot instantiate: " + clazz.getName(), e);
        }
    }
}
