package fr.olympus.hephaestus.factory;

import fr.olympus.hephaestus.materials.Material;

import java.util.List;

public abstract class Factory {

    protected List<Material> inputMaterials;
    protected List<Material> outputMaterials;


    protected Material tratedMaterial;
    protected boolean isOperating = false;

    protected byte[][][] layout;

    protected Factory(List<Material> inputMaterials, List<Material> outputMaterials) {
        this.inputMaterials = inputMaterials;
        this.outputMaterials = outputMaterials;
    }

    public void startFactory() {
        isOperating = true;
    }
    public void stopFactory() {
        isOperating = false;
    }

    public void setTratedMaterial(Material material) {
        this.tratedMaterial = material;
    }

    public Material getTratedMaterial() {
        return tratedMaterial;
    }

    public void setLayout(byte[][][] layout) {
        this.layout = layout;
    }

    public void update(float dt) {
        if (!isOperating) {
            return;
        }
        updateFactory(dt);
        if (this instanceof AutomaticFactory automaticFactory) {
            automaticFactory.updateAutomaticFactory(dt);
        }
    }

    protected abstract void updateFactory(float dt);





}
