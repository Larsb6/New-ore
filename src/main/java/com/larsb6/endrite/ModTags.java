package com.larsb6.endrite;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class ModTags {
	public static final TagKey<Block> INCORRECT_FOR_ENDRITE_TOOL = TagKey.of(RegistryKeys.BLOCK, Endrite.id("incorrect_for_endrite_tool"));

	public static final TagKey<Item> ENDRITE_TOOL_MATERIALS = TagKey.of(RegistryKeys.ITEM, Endrite.id("endrite_tool_materials"));
	public static final TagKey<Item> REPAIRS_ENDRITE_ARMOR = TagKey.of(RegistryKeys.ITEM, Endrite.id("repairs_endrite_armor"));

	private ModTags() {
	}
}
