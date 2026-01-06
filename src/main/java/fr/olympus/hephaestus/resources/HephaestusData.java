package fr.olympus.hephaestus.resources;

import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.materials.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HephaestusData {

    private Map<String, Material> materials = new HashMap<>();
    private Map<String, Factory> factories = new HashMap<>();


    public Map<String, Material> getMaterials() {
        return materials;
    }

    public Map<String, Factory> getFactories() {
        return factories;
    }

    public void registerMaterial(String id, Material material) {
        if (materials.containsKey(id)){
            throw new IllegalArgumentException("Material with id " + id + " and " + material.getName() + " is already registered.");
        }
        materials.put(id, material);
    }

    public void registerFactory(String id, Factory factory) {
        if (factories.containsKey(id)){
            throw new IllegalArgumentException("Factory with id " + id + " and " + factory.getName() + " is already registered.");
        }
        factories.put(id, factory);
    }

}
