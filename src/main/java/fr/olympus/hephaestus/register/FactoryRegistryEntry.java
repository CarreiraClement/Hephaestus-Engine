package fr.olympus.hephaestus.register;

import fr.olympus.hephaestus.factory.Factory;

import java.util.Set;
import java.util.function.Supplier;

public record FactoryRegistryEntry(String id, Set<String> groups, int level, Supplier<? extends Factory> supplier) {

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
