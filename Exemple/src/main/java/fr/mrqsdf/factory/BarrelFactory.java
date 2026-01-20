package fr.mrqsdf.factory;

import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.factory.FactoryAnnotation;

import static fr.mrqsdf.resources.Data.BARREL;
import static fr.mrqsdf.resources.Data.GROUP_BARREL;

/**
 * Barrel factory class.
 */
@FactoryAnnotation(id = BARREL, groups = {GROUP_BARREL}, level = 0)
public final class BarrelFactory extends Factory {
}
