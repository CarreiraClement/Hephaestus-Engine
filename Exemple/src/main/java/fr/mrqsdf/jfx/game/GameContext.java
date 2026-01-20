package fr.mrqsdf.jfx.game;

import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.materials.MaterialInstance;
import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.processing.ProcessRecipe;
import fr.olympus.hephaestus.processing.TimeWindow;
import fr.olympus.hephaestus.register.ProcessRecipeRegistryEntry;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.*;

/**
 * JAVAFX EXEMPLE 2D
 * <p>
 * Game context.
 * @param data
 * @param inventory
 */
public record GameContext(HephaestusData data, MaterialInventory inventory) {

    /**
     * Constructor.
     * @param data Hephaestus data
     * @param inventory Material inventory
     */
    public GameContext(HephaestusData data, MaterialInventory inventory) {
        this.data = Objects.requireNonNull(data, "data");
        this.inventory = Objects.requireNonNull(inventory, "inventory");
    }

    /**
     * Try to start production in the given factory with the given recipe.
     * Consumes required materials from inventory if successful.
     * @param factory Factory
     * @param recipe Process recipe
     * @return true if production started, false otherwise
     */
    public boolean tryStartProduction(Factory factory, ProcessRecipe recipe) {
        if (factory == null || recipe == null) return false;

        List<String> chosenIds = new ArrayList<>();
        for (MaterialMatcher matcher : recipe.inputs()) {
            String chosen = chooseOneMaterialIdForInputMatcher(matcher);
            if (chosen == null) return false;
            chosenIds.add(chosen);
        }

        Map<String, Integer> needed = new HashMap<>();
        for (String id : chosenIds) needed.merge(id, 1, Integer::sum);

        for (Map.Entry<String, Integer> e : needed.entrySet()) {
            if (!inventory.hasAtLeast(e.getKey(), e.getValue())) return false;
        }

        for (Map.Entry<String, Integer> e : needed.entrySet()) {
            if (!inventory.remove(e.getKey(), e.getValue())) return false;
        }

        for (String id : chosenIds) {
            factory.insert(new MaterialInstance(id, dummyVoxels()));
        }

        factory.setSession(recipe);
        factory.startFactory();
        return true;
    }

    /**
     * Tick the given factory, updating its state and extracting outputs to inventory.
     * @param factory Factory
     * @param dt Delta time in seconds
     * @return number of output materials extracted
     */
    public int tickFactory(Factory factory, float dt) {
        if (factory == null) return 0;

        factory.update(dt, data);

        List<MaterialInstance> out = factory.extractAllOutputs();
        if (out.isEmpty()) return 0;

        for (MaterialInstance mi : out) {
            inventory.add(mi.materialId(), 1);
        }
        return out.size();
    }

    /**
     * Get recipe entry by ID.
     * @param recipeId Recipe ID
     * @return Recipe entry or null if not found
     */
    public ProcessRecipeRegistryEntry getRecipeEntry(String recipeId) {
        return data.getProcessRecipeById(recipeId);
    }

    /**
     * Get all recipes that can be processed in the given factory.
     * @param factory Factory
     * @return List of recipe entries
     */
    public List<ProcessRecipeRegistryEntry> recipesForFactory(Factory factory) {
        return data.getProcessRecipesByFactoryId(factory.getRegistryId(), factory.getRegistryGroups(), factory.getRegistryLevel());
    }

    /**
     * Grant outputs from the given recipe to inventory.
     * @param recipe Process recipe
     * @return number of output materials granted
     */
    public int grantOutputsFromRecipe(ProcessRecipe recipe) {
        if (recipe == null) return 0;

        int added = 0;
        for (MaterialMatcher matcher : recipe.outputs()) {
            String outId = chooseOneMaterialIdForOutputMatcher(matcher);
            if (outId == null) continue;
            inventory.add(outId, 1);
            added++;
        }
        return added;
    }

    /**
     * Get minimum seconds required for the given recipe.
     * @param recipe Process recipe
     * @return Minimum seconds, or 0 if recipe is null or has no time window
     */
    public float minSecondsOrZero(ProcessRecipe recipe) {
        if (recipe == null) return 0f;
        TimeWindow w = recipe.timeWindowOrNull();
        return w == null ? 0f : w.minSeconds();
    }

    /**
     * Choose one material ID matching the given input matcher that is available in inventory.
     * @param matcher Material matcher
     * @return Material ID or null if none found
     */
    private String chooseOneMaterialIdForInputMatcher(MaterialMatcher matcher) {
        if (matcher == null) return null;

        return switch (matcher.getKind()) {
            case ID -> {
                String id = matcher.getMaterialId();
                yield (id != null && inventory.getAmount(id) > 0) ? id : null;
            }
            case ANY -> firstAnyAvailable();
            case ANY_OF_CATEGORIES -> firstMatchingAnyOfAvailable(matcher.getCategoryKeys());
            case ALL_OF_CATEGORIES -> firstMatchingAllOfAvailable(matcher.getCategoryKeys());
        };
    }

    /** Choose one material ID matching the given output matcher from registered materials.
     * @param matcher Material matcher
     * @return Material ID or null if none found
     */
    private String chooseOneMaterialIdForOutputMatcher(MaterialMatcher matcher) {
        if (matcher == null) return null;

        return switch (matcher.getKind()) {
            case ID -> matcher.getMaterialId();
            case ANY -> firstAnyRegistered();
            case ANY_OF_CATEGORIES -> firstMatchingAnyOfRegistered(matcher.getCategoryKeys());
            case ALL_OF_CATEGORIES -> firstMatchingAllOfRegistered(matcher.getCategoryKeys());
        };
    }

    /**
     * Find first available material ID.
     * @return Material ID or null if none available
     */
    private String firstAnyAvailable() {
        for (String id : data.getAllMaterialIds()) {
            if (inventory.getAmount(id) > 0) return id;
        }
        return null;
    }

    /**
     * Find first registered material ID.
     * @return Material ID or null if none registered
     */
    private String firstAnyRegistered() {
        for (String id : data.getAllMaterialIds()) {
            return id;
        }
        return null;
    }

    /** Find first available material ID matching any of the wanted categories.
     * @param wanted Set of wanted category keys
     * @return Material ID or null if none found
     */
    private String firstMatchingAnyOfAvailable(Set<String> wanted) {
        if (wanted == null || wanted.isEmpty()) return null;
        for (String id : data.getAllMaterialIds()) {
            if (inventory.getAmount(id) <= 0) continue;
            Set<String> keys = data.getMaterialCategoryKeys(id);
            for (String w : wanted) {
                if (keys.contains(w)) return id;
            }
        }
        return null;
    }

    /** Find first available material ID matching all of the wanted categories.
     * @param wanted Set of wanted category keys
     * @return Material ID or null if none found
     */
    private String firstMatchingAllOfAvailable(Set<String> wanted) {
        if (wanted == null || wanted.isEmpty()) return null;
        for (String id : data.getAllMaterialIds()) {
            if (inventory.getAmount(id) <= 0) continue;
            Set<String> keys = data.getMaterialCategoryKeys(id);
            if (keys.containsAll(wanted)) return id;
        }
        return null;
    }

    /** Find first registered material ID matching any of the wanted categories.
     * @param wanted Set of wanted category keys
     * @return Material ID or null if none found
     */
    private String firstMatchingAnyOfRegistered(Set<String> wanted) {
        if (wanted == null || wanted.isEmpty()) return null;
        for (String id : data.getAllMaterialIds()) {
            Set<String> keys = data.getMaterialCategoryKeys(id);
            for (String w : wanted) {
                if (keys.contains(w)) return id;
            }
        }
        return null;
    }

    /** Find first registered material ID matching all of the wanted categories.
     * @param wanted Set of wanted category keys
     * @return Material ID or null if none found
     */
    private String firstMatchingAllOfRegistered(Set<String> wanted) {
        if (wanted == null || wanted.isEmpty()) return null;
        for (String id : data.getAllMaterialIds()) {
            Set<String> keys = data.getMaterialCategoryKeys(id);
            if (keys.containsAll(wanted)) return id;
        }
        return null;
    }

    /** Dummy voxel data for inserted materials.
     * @return Dummy voxel data
     */
    private static byte[][][] dummyVoxels() {
        return new byte[1][1][1];
    }
}
