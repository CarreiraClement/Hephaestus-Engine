package fr.mrqsdf.factory;

import fr.olympus.hephaestus.factory.Factory;
import fr.olympus.hephaestus.factory.FactoryAnnotation;

import static fr.mrqsdf.resources.Data.FURNACE_STONE;
import static fr.mrqsdf.resources.Data.GROUP_FURNACE;

/**
 * Stone Furnace factory class.
 */
@FactoryAnnotation(id = FURNACE_STONE, groups = {GROUP_FURNACE}, level = 1)
public final class StoneFurnaceFactory extends Factory {
}
