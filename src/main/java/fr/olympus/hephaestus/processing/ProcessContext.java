package fr.olympus.hephaestus.processing;

import fr.olympus.hephaestus.materials.MaterialInstance;

import java.util.List;

/**
 * :
 * Execution context for a processing operation.
 *
 * @param contents the input materials to be processed
 * @param outputs  the resulting materials after processing
 */
public record ProcessContext(List<MaterialInstance> contents, List<MaterialInstance> outputs) {

    /**
     * Adds a processed material to the outputs list.
     *
     * @param out the material instance to add to outputs
     */
    public void pushOutput(MaterialInstance out) {
        outputs.add(out);
    }

    /**
     * Removes a material from the contents list at the specified index.
     *
     * @param idx the index of the material to remove from contents
     */
    public void removeContentAt(int idx) {
        contents.remove(idx);
    }
}
