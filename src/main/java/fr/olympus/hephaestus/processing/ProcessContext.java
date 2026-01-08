package fr.olympus.hephaestus.processing;

import fr.olympus.hephaestus.materials.MaterialInstance;

import java.util.List;

public final class ProcessContext {

    private final List<MaterialInstance> contents;
    private final List<MaterialInstance> outputs;

    public ProcessContext(List<MaterialInstance> contents, List<MaterialInstance> outputs) {
        this.contents = contents;
        this.outputs = outputs;
    }

    public List<MaterialInstance> contents() {
        return contents;
    }

    public List<MaterialInstance> outputs() {
        return outputs;
    }

    // helpers optionnels
    public void pushOutput(MaterialInstance out) {
        outputs.add(out);
    }

    public void removeContentAt(int idx) {
        contents.remove(idx);
    }
}
