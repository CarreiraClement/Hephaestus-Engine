package fr.mrqsdf.factory;

import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.factory.FactoryAnnotation;

import static fr.mrqsdf.resources.Data.ANVIL_IRON;
import static fr.mrqsdf.resources.Data.GROUP_ANVIL;

/**
 * Iron Anvil factory class.
 */
@FactoryAnnotation(id = ANVIL_IRON, groups = {GROUP_ANVIL}, level = 2)
public final class IronAnvilFactory extends Factory {
}
