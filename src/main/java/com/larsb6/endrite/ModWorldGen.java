package com.larsb6.endrite;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public final class ModWorldGen {
	/** Defined in data/endrite/worldgen/placed_feature/ore_endrite.json; replaces end stone only. */
	public static final RegistryKey<PlacedFeature> ORE_ENDRITE = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Endrite.id("ore_endrite"));

	static void initialize() {
		BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_ENDRITE);
	}

	private ModWorldGen() {
	}
}
