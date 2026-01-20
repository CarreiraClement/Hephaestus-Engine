package fr.mrqsdf.jfx.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Material inventory class
 */
public final class MaterialInventory {

    /** Map of material IDs to their amounts */
    private final Map<String, IntegerProperty> amounts = new ConcurrentHashMap<>();

    /** Constructor */
    public IntegerProperty amountProperty(String materialId) {
        return amounts.computeIfAbsent(materialId, k -> new SimpleIntegerProperty(0));
    }

    /**
     * Get the amount of the given material ID.
     * @param materialId Material ID
     * @return Amount
     */
    public int getAmount(String materialId) {
        IntegerProperty p = amounts.get(materialId);
        return p == null ? 0 : p.get();
    }

    /**
     * Set the amount of the given material ID.
     * @param materialId Material ID
     * @param amount Amount
     */
    public void setAmount(String materialId, int amount) {
        if (amount < 0) amount = 0;
        amountProperty(materialId).set(amount);
    }

    /**
     * Add the given amount to the material ID.
     * @param materialId Material ID
     * @param delta Amount to add
     */
    public void add(String materialId, int delta) {
        if (delta <= 0) return;
        IntegerProperty p = amountProperty(materialId);
        p.set(p.get() + delta);
    }

    /**
     * Remove the given amount from the material ID.
     * @param materialId Material ID
     * @param delta Amount to remove
     * @return true if successful, false if not enough material
     */
    public boolean remove(String materialId, int delta) {
        if (delta <= 0) return true;
        IntegerProperty p = amountProperty(materialId);
        int cur = p.get();
        if (cur < delta) return false;
        p.set(cur - delta);
        return true;
    }

    /**
     * Check if the inventory has at least the given amount of the material ID.
     * @param materialId Material ID
     * @param amount Amount to check
     * @return true if enough material, false otherwise
     */
    public boolean hasAtLeast(String materialId, int amount) {
        return getAmount(materialId) >= amount;
    }

    /**
     * Get the set of known material IDs in the inventory.
     * @return Set of material IDs
     */
    public Set<String> knownMaterialIds() {
        return Collections.unmodifiableSet(amounts.keySet());
    }
}
