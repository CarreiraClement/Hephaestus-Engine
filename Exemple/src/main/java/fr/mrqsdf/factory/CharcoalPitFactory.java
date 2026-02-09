package fr.mrqsdf.factory;

import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.factory.FactoryAnnotation;
import fr.olympus.hephaestus.processing.MaterialMatcher;

import java.util.List;

import static fr.mrqsdf.resources.Data.CHARCOAL_PIT;
import static fr.mrqsdf.resources.Data.GROUP_CHARCOAL;

/**
 * Charcoal Pit factory class.
 */
@FactoryAnnotation(id = CHARCOAL_PIT, groups = {GROUP_CHARCOAL}, level = 0)
public final class CharcoalPitFactory extends Factory {

    @Override
    public void processFinished(List<MaterialMatcher> outputs) {

    }
}
