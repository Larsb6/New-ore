package com.larsb6.endrite;

import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public final class ModBlocks {
	public static final Block ENDRITE_ORE = register(
		"endrite_ore",
		settings -> new ExperienceDroppingBlock(UniformIntProvider.create(4, 8), settings),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.PALE_YELLOW)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(10.0F, 1200.0F)
			.sounds(BlockSoundGroup.ANCIENT_DEBRIS),
		false
	);
	public static final Block RAW_ENDRITE_BLOCK = register(
		"raw_endrite_block",
		Block::new,
		AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(5.0F, 6.0F),
		false
	);
	public static final Block ENDRITE_BLOCK = register(
		"endrite_block",
		Block::new,
		AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).requiresTool().strength(50.0F, 1200.0F).sounds(BlockSoundGroup.NETHERITE),
		true
	);

	private static Block register(String name, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings, boolean fireproof) {
		RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, Endrite.id(name));
		Block block = Registry.register(Registries.BLOCK, blockKey, factory.apply(settings.registryKey(blockKey)));

		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Endrite.id(name));
		Item.Settings itemSettings = new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey();
		if (fireproof) {
			itemSettings.fireproof();
		}
		Registry.register(Registries.ITEM, itemKey, new BlockItem(block, itemSettings));
		return block;
	}

	static void initialize() {
	}

	private ModBlocks() {
	}
}
