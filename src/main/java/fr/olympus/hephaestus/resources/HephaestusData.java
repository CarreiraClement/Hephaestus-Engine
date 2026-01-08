package fr.olympus.hephaestus.resources;

import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.materials.Material;
import fr.olympus.hephaestus.materials.MaterialCategory;
import fr.olympus.hephaestus.processing.ProcessRecipe;
import fr.olympus.hephaestus.register.FactoryRegistryEntry;
import fr.olympus.hephaestus.register.ProcessRecipeRegistryEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class HephaestusData {

    private final Map<String, Material> materials = new ConcurrentHashMap<>();

    private final Map<String, FactoryRegistryEntry> factories = new ConcurrentHashMap<>();
    private final List<ProcessRecipeRegistryEntry> recipeEntries = Collections.synchronizedList(new ArrayList<>());

    public Map<String, Material> getMaterials() {
        return Collections.unmodifiableMap(materials);
    }

    public void registerMaterial(String id, Material material) {
        if (materials.putIfAbsent(id, material) != null) {
            throw new IllegalArgumentException("Material already registered: " + id);
        }
    }

    public void registerFactory(FactoryRegistryEntry entry) {
        if (entry == null) throw new IllegalArgumentException("entry cannot be null.");
        if (factories.putIfAbsent(entry.id(), entry) != null) {
            throw new IllegalArgumentException("Factory already registered: " + entry.id());
        }
    }

    public void registerProcessRecipe(ProcessRecipeRegistryEntry entry) {
        if (entry == null) throw new IllegalArgumentException("entry cannot be null.");
        recipeEntries.add(entry);
    }

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

    public Material getMaterialDef(String id) {
        Material m = materials.get(id);
        if (m == null) throw new IllegalArgumentException("Unknown material id: " + id);
        return m;
    }

    public Set<String> getMaterialCategoryKeys(String id) {
        Material m = getMaterialDef(id);
        Set<String> keys = new HashSet<>();
        for (MaterialCategory c : m.getCategories()) {
            if (c instanceof Enum<?> e) keys.add(e.name());
        }
        return keys;
    }

    public Set<String> getAllMaterialIds() {
        return Collections.unmodifiableSet(materials.keySet());
    }

    // utile pour le planning: récupérer toutes les recipes (snapshot)
    public List<ProcessRecipeRegistryEntry> getProcessRecipeEntriesSnapshot() {
        synchronized (recipeEntries) {
            return List.copyOf(recipeEntries);
        }
    }
}
