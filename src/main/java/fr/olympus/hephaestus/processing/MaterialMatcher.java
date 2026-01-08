package fr.olympus.hephaestus.processing;

import fr.olympus.hephaestus.materials.MaterialCategory;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public final class MaterialMatcher {

    public enum Kind {
        ID,
        ANY_OF_CATEGORIES,
        ALL_OF_CATEGORIES,
        ANY
    }

    private final Kind kind;
    private final String materialId;          // pour ID
    private final Set<String> categoryKeys;   // enum.name() triés

    private MaterialMatcher(Kind kind, String materialId, Set<String> categoryKeys) {
        this.kind = Objects.requireNonNull(kind, "kind");
        this.materialId = materialId;
        this.categoryKeys = categoryKeys == null ? null : Set.copyOf(categoryKeys);
    }

    public static MaterialMatcher any() {
        return new MaterialMatcher(Kind.ANY, null, null);
    }

    public static MaterialMatcher id(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be null/blank.");
        return new MaterialMatcher(Kind.ID, id, null);
    }

    public static MaterialMatcher anyOfCategories(Set<? extends MaterialCategory> categories) {
        return new MaterialMatcher(Kind.ANY_OF_CATEGORIES, null, toKeys(categories));
    }

    public static MaterialMatcher allOfCategories(Set<? extends MaterialCategory> categories) {
        return new MaterialMatcher(Kind.ALL_OF_CATEGORIES, null, toKeys(categories));
    }

    private static Set<String> toKeys(Set<? extends MaterialCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("categories cannot be null/empty.");
        }
        TreeSet<String> keys = new TreeSet<>();
        for (MaterialCategory c : categories) {
            if (c == null) continue;
            if (!(c instanceof Enum<?> e)) {
                throw new IllegalArgumentException("MaterialCategory must be an enum: " + c);
            }
            keys.add(e.name());
        }
        return keys;
    }

    public Kind getKind() { return kind; }
    public String getMaterialId() { return materialId; }
    public Set<String> getCategoryKeys() { return categoryKeys; }

    public int specificityScore() {
        return switch (kind) {
            case ID -> 1000;
            case ALL_OF_CATEGORIES -> 200;
            case ANY_OF_CATEGORIES -> 100;
            case ANY -> 0;
        };
    }

    public String key() {
        return switch (kind) {
            case ANY -> "ANY";
            case ID -> "ID:" + materialId;
            case ANY_OF_CATEGORIES -> "CAT_ANY:" + categoryKeys;
            case ALL_OF_CATEGORIES -> "CAT_ALL:" + categoryKeys;
        };
    }

    @Override public String toString() { return key(); }
    @Override public int hashCode() { return Objects.hash(key()); }
    @Override public boolean equals(Object o) {
        return (o instanceof MaterialMatcher other) && Objects.equals(this.key(), other.key());
    }
}
