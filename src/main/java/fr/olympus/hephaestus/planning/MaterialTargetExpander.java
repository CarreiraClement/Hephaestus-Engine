package fr.olympus.hephaestus.planning;

import fr.olympus.hephaestus.processing.MaterialMatcher;
import fr.olympus.hephaestus.resources.HephaestusData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utils for expanding material targets.
 */
public final class MaterialTargetExpander {

    // Private constructor to prevent instantiation

    /**
     * Private constructor to prevent instantiation.
     */
    private MaterialTargetExpander() {
    }

    /**
     * Transforms a "category" target into multiple "ID" targets (all compatible materials).
     * If the target is already ID/ANY, returns it as is.
     *
     * @param target the target to expand
     * @param data   the HephaestusData instance to query materials
     * @param limit  the maximum number of concrete IDs to return
     * @return a list of concrete ID matchers
     */
    public static List<MaterialMatcher> expandToConcreteIds(MaterialMatcher target,
                                                            HephaestusData data,
                                                            int limit) {
        if (limit <= 0) throw new IllegalArgumentException("limit must be > 0.");

        return switch (target.getKind()) {
            case ANY -> List.of(MaterialMatcher.any());
            case ID -> List.of(target);

            case ANY_OF_CATEGORIES -> {
                List<MaterialMatcher> out = new ArrayList<>();
                Set<String> wanted = target.getCategoryKeys();

                for (String id : data.getAllMaterialIds()) {
                    Set<String> cats = data.getMaterialCategoryKeys(id);
                    boolean ok = false;
                    for (String w : wanted) {
                        if (cats.contains(w)) {
                            ok = true;
                            break;
                        }
                    }
                    if (ok) {
                        out.add(MaterialMatcher.id(id));
                        if (out.size() >= limit) break;
                    }
                }
                yield out;
            }

            case ALL_OF_CATEGORIES -> {
                List<MaterialMatcher> out = new ArrayList<>();
                Set<String> wanted = target.getCategoryKeys();

                for (String id : data.getAllMaterialIds()) {
                    Set<String> cats = data.getMaterialCategoryKeys(id);
                    if (cats.containsAll(wanted)) {
                        out.add(MaterialMatcher.id(id));
                        if (out.size() >= limit) break;
                    }
                }
                yield out;
            }
        };
    }
}
