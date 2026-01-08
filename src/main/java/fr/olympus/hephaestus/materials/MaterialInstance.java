package fr.olympus.hephaestus.materials;

import java.util.Objects;

public record MaterialInstance(String materialId, byte[][][] voxels) {

    public MaterialInstance(String materialId, byte[][][] voxels) {
        if (materialId == null || materialId.isBlank()) {
            throw new IllegalArgumentException("materialId cannot be null/blank.");
        }
        this.materialId = materialId;
        this.voxels = Objects.requireNonNull(voxels, "voxels");
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialId);
    }

    @Override
    public String toString() {
        return "MaterialInstance{" +
                "materialId='" + materialId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MaterialInstance other = (MaterialInstance) obj;
        return materialId.equals(other.materialId);
    }
}
