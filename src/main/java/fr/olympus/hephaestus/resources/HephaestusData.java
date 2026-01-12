package fr.olympus.hephaestus.resources;

import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.materials.Material;
import fr.olympus.hephaestus.materials.MaterialCategory;
import fr.olympus.hephaestus.processing.ProcessRecipe;
import fr.olympus.hephaestus.register.FactoryRegistryEntry;
import fr.olympus.hephaestus.register.ProcessRecipeRegistryEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry for materials, factories, and process recipes in the Hephaestus system.
 */
public final class HephaestusData {

    /**
     * Map of registered materials by their unique IDs.
     */
    private final Map<String, Material> materials = new ConcurrentHashMap<>();

    /**
     * Map of registered factory entries by their unique IDs.
     */
    private final Map<String, FactoryRegistryEntry> factories = new ConcurrentHashMap<>();
    /**
     * List of registered process recipe entries.
     */
    private final List<ProcessRecipeRegistryEntry> recipeEntries = Collections.synchronizedList(new ArrayList<>());

    /**
     * Constructs a new HephaestusData instance.
     */
    public HephaestusData() {
    }

    /**
     * Retrieves an unmodifiable view of the registered materials.
     *
     * @return Map of material IDs to Material instances.
     */
    public Map<String, Material> getMaterials() {
        return Collections.unmodifiableMap(materials);
    }

    /**
     * Registers a new material with the given ID.
     *
     * @param id       Unique identifier for the material.
     * @param material Material instance to register.
     * @throws IllegalArgumentException if the material ID is already registered.
     */
    public void registerMaterial(String id, Material material) {
        if (materials.putIfAbsent(id, material) != null) {
            throw new IllegalArgumentException("Material already registered: " + id);
        }
    }

    /**
     * Registers a new factory entry.
     *
     * @param entry FactoryRegistryEntry to register.
     * @throws IllegalArgumentException if the entry is null or the factory ID is already registered.
     */
    public void registerFactory(FactoryRegistryEntry entry) {
        if (entry == null) throw new IllegalArgumentException("entry cannot be null.");
        if (factories.putIfAbsent(entry.id(), entry) != null) {
            throw new IllegalArgumentException("Factory already registered: " + entry.id());
        }
    }

    /**
     * Registers a new process recipe entry.
     *
     * @param entry ProcessRecipeRegistryEntry to register.
     * @throws IllegalArgumentException if the entry is null.
     */
    public void registerProcessRecipe(ProcessRecipeRegistryEntry entry) {
        if (entry == null) throw new IllegalArgumentException("entry cannot be null.");
        recipeEntries.add(entry);
    }

    /**
     * Creates a new factory instance based on the registered factory ID.
     * Attaches all compatible process recipes to the factory.
     *
     * @param factoryId The unique identifier of the factory to create.
     * @return A new Factory instance with attached compatible process recipes.
     */
    public Factory createFactory(String factoryId) {
        FactoryRegistryEntry reg = factories.get(factoryId);
        if (reg == null) throw new IllegalArgumentException("Unknown factory id: " + factoryId);

        Factory instance = reg.supplier().get();
        if (instance == null) throw new IllegalStateException("Factory supplier returned null: " + factoryId);

        instance.setRegistryMeta(reg.id(), reg.groups(), reg.level());

        // Attacher toutes les process-recipes compatibles (id/group/level)
        List<ProcessRecipe> attach = new ArrayList<>();
        synchronized (recipeEntries) {
            for (ProcessRecipeRegistryEntry re : recipeEntries) {
                if (re.selector().matchesFactory(reg.id(), reg.groups(), reg.level())) {
                    attach.add(re.recipe());
                }
            }
        }
        instance.addRecipes(attach);

        return instance;
    }

    /**
     * Retrieves the material definition for the given material ID.
     *
     * @param id Unique identifier of the material.
     * @return Material instance associated with the given ID.
     * @throws IllegalArgumentException if the material ID is unknown.
     */
    public Material getMaterialDef(String id) {
        Material m = materials.get(id);
        if (m == null) throw new IllegalArgumentException("Unknown material id: " + id);
        return m;
    }

    /**
     * Retrieves the set of material category keys for the given material ID.
     *
     * @param id Unique identifier of the material.
     * @return Set of category keys associated with the material.
     * @throws IllegalArgumentException if the material ID is unknown.
     */
    public Set<String> getMaterialCategoryKeys(String id) {
        Material m = getMaterialDef(id);
        Set<String> keys = new HashSet<>();
        for (MaterialCategory c : m.getCategories()) {
            if (c instanceof Enum<?> e) keys.add(e.name());
        }
        return keys;
    }

    /**
     * Retrieves an unmodifiable set of all registered material IDs.
     *
     * @return Set of all material IDs.
     */
    public Set<String> getAllMaterialIds() {
        return Collections.unmodifiableSet(materials.keySet());
    }

    /**
     * Retrieves a snapshot list of all registered process recipe entries.
     *
     * @return List of ProcessRecipeRegistryEntry instances.
     */
    public List<ProcessRecipeRegistryEntry> getProcessRecipeEntriesSnapshot() {
        synchronized (recipeEntries) {
            return List.copyOf(recipeEntries);
        }
    }

    /**
     * Retrieves all process recipes compatible with the given factory ID, groups, and level.
     *
     * @param factoryId               Unique identifier of the factory.
     * @param factoryGroupsOfInstance Set of groups associated with the factory instance.
     * @param factoryLevel            Level of the factory instance.
     * @return List of compatible ProcessRecipeRegistryEntry instances.
     */
    public List<ProcessRecipeRegistryEntry> getProcessRecipesByFactoryId(String factoryId, Set<String> factoryGroupsOfInstance, int factoryLevel) {
        List<ProcessRecipeRegistryEntry> results = new ArrayList<>();
        synchronized (recipeEntries) {
            for (ProcessRecipeRegistryEntry re : recipeEntries) {
                if (re.selector().matchesFactory(factoryId, factoryGroupsOfInstance, factoryLevel)) {
                    results.add(re);
                }
            }
        }
        return results;
    }

    /**
     * Retrieves a process recipe entry by its unique recipe ID.
     *
     * @param recipeId Unique identifier of the recipe.
     * @return ProcessRecipeRegistryEntry associated with the given recipe ID.
     * @throws IllegalArgumentException if the recipe ID is unknown.
     */
    public ProcessRecipeRegistryEntry getProcessRecipeById(String recipeId) {
        synchronized (recipeEntries) {
            for (ProcessRecipeRegistryEntry re : recipeEntries) {
                if (re.recipe().id().equals(recipeId)) {
                    return re;
                }
            }
        }
        throw new IllegalArgumentException("Unknown recipe id: " + recipeId);
    }

    /**
     * Retrieves a factory registry entry by its unique factory ID.
     *
     * @param factoryId Unique identifier of the factory.
     * @return FactoryRegistryEntry associated with the given factory ID.
     * @throws IllegalArgumentException if the factory ID is unknown.
     */
    public FactoryRegistryEntry getFactoryRegistryEntryById(String factoryId) {
        FactoryRegistryEntry entry = factories.get(factoryId);
        if (entry == null) {
            throw new IllegalArgumentException("Unknown factory id: " + factoryId);
        }
        return entry;
    }

    /**
     * Retrieves a sorted snapshot list of all registered factory IDs.
     *
     * @return List of factory IDs.
     */
    public List<String> getFactoryIdsSnapshot() {
        ArrayList<String> ids = new ArrayList<>(factories.keySet());
        Collections.sort(ids);
        return ids;
    }
}
