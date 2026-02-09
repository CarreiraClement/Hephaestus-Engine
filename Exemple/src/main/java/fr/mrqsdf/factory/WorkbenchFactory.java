package fr.mrqsdf.factory;

import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.factory.FactoryAnnotation;
import fr.olympus.hephaestus.processing.MaterialMatcher;

import java.util.List;

import static fr.mrqsdf.resources.Data.GROUP_WORKBENCH;
import static fr.mrqsdf.resources.Data.WORKBENCH;

/**
 * Workbench factory class.
 */
@FactoryAnnotation(id = WORKBENCH, groups = {GROUP_WORKBENCH}, level = 0)
public final class WorkbenchFactory extends Factory {

    @Override
    public void processFinished(List<MaterialMatcher> outputs) {

    }
}
