package fr.olympus.hephaestus.processing;

import fr.olympus.hephaestus.materials.MaterialCategory;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * A matcher for materials based on different criteria.
 */
public final class MaterialMatcher {

    /**
     * The kind of matching to be performed.
     */
    public enum Kind {
        /**
         * Match by specific material ID.
         */
        ID,
        /**
         * Match if any of the specified categories are present.
         */
        ANY_OF_CATEGORIES,
        /**
         * Match only if all of the specified categories are present.
         */
        ALL_OF_CATEGORIES,
        /**
         * Match any material.
         */
        ANY
    }

    // Fields
    /**
     * The kind of matching to be performed.
     */
    private final Kind kind;
    /**
     * The material ID for ID-based matching.
     */
    private final String materialId;          // pour ID
    /**
     * The set of category keys for category-based matching.
     */
    private final Set<String> categoryKeys;   // enum.name() triés

    // Constructor

    /**
     * Private constructor to initialize the MaterialMatcher.
     */
    private MaterialMatcher(Kind kind, String materialId, Set<String> categoryKeys) {
        this.kind = Objects.requireNonNull(kind, "kind");
        this.materialId = materialId;
        this.categoryKeys = categoryKeys == null ? null : Set.copyOf(categoryKeys);
    }

    // Static factory methods

    /**
     * Creates a matcher that matches any material.
     *
     * @return a MaterialMatcher that matches any material
     */
    public static MaterialMatcher any() {
        return new MaterialMatcher(Kind.ANY, null, null);
    }

    /**
     * Creates a matcher that matches a specific material by its ID.
     *
     * @param id the unique identifier of the material
     * @return a MaterialMatcher that matches the specified material ID
     */
    public static MaterialMatcher id(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be null/blank.");
        return new MaterialMatcher(Kind.ID, id, null);
    }

    /**
     * Creates a matcher that matches if any of the specified categories are present.
     *
     * @param categories the set of material categories to match against
     * @return a MaterialMatcher that matches if any of the specified categories are present
     */
    public static MaterialMatcher anyOfCategories(Set<? extends MaterialCategory> categories) {
        return new MaterialMatcher(Kind.ANY_OF_CATEGORIES, null, toKeys(categories));
    }

    /**
     * Creates a matcher that matches only if all of the specified categories are present.
     *
     * @param categories the set of material categories to match against
     * @return a MaterialMatcher that matches only if all of the specified categories are present
     */
    public static MaterialMatcher allOfCategories(Set<? extends MaterialCategory> categories) {
        return new MaterialMatcher(Kind.ALL_OF_CATEGORIES, null, toKeys(categories));
    }

    // Helper method to convert categories to their string keys

    /**
     * Converts a set of MaterialCategory enums to their string keys.
     *
     * @param categories the set of material categories
     * @return a set of string keys representing the categories
     */
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

    // Getters

    /**
     * Returns the kind of matching to be performed.
     *
     * @return the kind of matching
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Returns the material ID for ID-based matching.
     *
     * @return the material ID
     */
    public String getMaterialId() {
        return materialId;
    }

    /**
     * Returns the set of category keys for category-based matching.
     *
     * @return the set of category keys
     */
    public Set<String> getCategoryKeys() {
        return categoryKeys;
    }

    // Other methods

    /**
     * Computes a specificity score for the matcher based on its kind.
     *
     * @return the specificity score
     */
    public int specificityScore() {
        return switch (kind) {
            case ID -> 1000;
            case ALL_OF_CATEGORIES -> 200;
            case ANY_OF_CATEGORIES -> 100;
            case ANY -> 0;
        };
    }

    /**
     * Generates a unique key representing the matcher.
     *
     * @return the unique key for the matcher
     */
    public String key() {
        return switch (kind) {
            case ANY -> "ANY";
            case ID -> "ID:" + materialId;
            case ANY_OF_CATEGORIES -> "CAT_ANY:" + categoryKeys;
            case ALL_OF_CATEGORIES -> "CAT_ALL:" + categoryKeys;
        };
    }
    // Overrides

    /**
     * Returns the string representation of the matcher.
     *
     * @return the string representation of the matcher
     */
    @Override
    public String toString() {
        return key();
    }

    /**
     * Computes the hash code based on the matcher's key.
     *
     * @return the hash code of the matcher
     */
    @Override
    public int hashCode() {
        return Objects.hash(key());
    }

    /**
     * Checks equality based on the matcher's key.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        return (o instanceof MaterialMatcher other) && Objects.equals(this.key(), other.key());
    }
}
