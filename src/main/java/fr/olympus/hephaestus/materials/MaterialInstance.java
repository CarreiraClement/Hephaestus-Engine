package fr.olympus.hephaestus.materials;

import java.util.Objects;

/**
 * :
 * Represents an instance of a material with its unique identifier and voxel data.
 *
 * @param materialId the unique identifier of the material
 * @param voxels     the 3D array representing voxel data of the material
 */
public record MaterialInstance(String materialId, byte[][][] voxels) {

    /**
     * Constructs a MaterialInstance with the specified materialId and voxel data.
     *
     * @param materialId the unique identifier of the material
     * @param voxels     the 3D array representing voxel data of the material
     * @throws IllegalArgumentException if materialId is null/blank
     * @throws NullPointerException     if voxels is null
     */
    public MaterialInstance(String materialId, byte[][][] voxels) {
        if (materialId == null || materialId.isBlank()) {
            throw new IllegalArgumentException("materialId cannot be null/blank.");
        }
        this.materialId = materialId;
        this.voxels = Objects.requireNonNull(voxels, "voxels");
    }

    /**
     * Computes the hash code for this MaterialInstance based on its materialId.
     *
     * @return the hash code of the materialId
     */
    @Override
    public int hashCode() {
        return Objects.hash(materialId);
    }

    /**
     * Returns a string representation of the MaterialInstance.
     *
     * @return a string containing the materialId
     */
    @Override
    public String toString() {
        return "MaterialInstance{" +
                "materialId='" + materialId + '\'' +
                '}';
    }

    /**
     * Compares this MaterialInstance to another object for equality based on materialId.
     *
     * @param obj the object to compare with
     * @return true if the other object is a MaterialInstance with the same materialId, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MaterialInstance other = (MaterialInstance) obj;
        return materialId.equals(other.materialId);
    }
}
