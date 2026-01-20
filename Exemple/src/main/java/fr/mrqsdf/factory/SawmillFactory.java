package fr.mrqsdf.factory;

import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.factory.FactoryAnnotation;

import static fr.mrqsdf.resources.Data.GROUP_SAWMILL;
import static fr.mrqsdf.resources.Data.SAWMILL;

/**
 * Sawmill factory class.
 */
@FactoryAnnotation(id = SAWMILL, groups = {GROUP_SAWMILL}, level = 0)
public final class SawmillFactory extends Factory {
}
