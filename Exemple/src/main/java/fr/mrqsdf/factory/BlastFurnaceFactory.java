package fr.mrqsdf.factory;

import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.factory.FactoryAnnotation;
import fr.olympus.hephaestus.processing.MaterialMatcher;

import java.util.List;

import static fr.mrqsdf.resources.Data.FURNACE_BLAST;
import static fr.mrqsdf.resources.Data.GROUP_FURNACE;

/**
 * Blast Furnace factory class.
 */
@FactoryAnnotation(id = FURNACE_BLAST, groups = {GROUP_FURNACE}, level = 2)
public final class BlastFurnaceFactory extends Factory {

    @Override
    public void processFinished(List<MaterialMatcher> outputs) {

    }
}
