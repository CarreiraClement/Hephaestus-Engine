package fr.olympus.hephaestus.register;

import fr.olympus.hephaestus.factory.Factory;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Represents an entry in the factory registry.
 *
 * @param id       The unique identifier for the factory.
 * @param groups   The groups associated with the factory.
 * @param level    The level of the factory.
 * @param supplier A supplier that provides instances of the factory.
 */
public record FactoryRegistryEntry(String id, Set<String> groups, int level, Supplier<? extends Factory> supplier) {

    /**
     * Constructs a FactoryRegistryEntry with the specified parameters.
     *
     * @param id       The unique identifier for the factory.
     * @param groups   The groups associated with the factory.
     * @param level    The level of the factory.
     * @param supplier A supplier that provides instances of the factory.
     * @throws IllegalArgumentException if id is null/blank, groups is null, or supplier is null.
     */
    public FactoryRegistryEntry(String id, Set<String> groups, int level, Supplier<? extends Factory> supplier) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be null/blank.");
        if (groups == null) throw new IllegalArgumentException("groups cannot be null.");
        if (supplier == null) throw new IllegalArgumentException("supplier cannot be null.");
        this.id = id;
        this.groups = Set.copyOf(groups);
        this.level = level;
        this.supplier = supplier;
    }
}
