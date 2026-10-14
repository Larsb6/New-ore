package com.larsb6.endrite;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

public final class ModItemGroups {
	static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
			entries.addAfter(Items.ANCIENT_DEBRIS, ModBlocks.ENDRITE_ORE);
			entries.addAfter(Items.RAW_GOLD_BLOCK, ModBlocks.RAW_ENDRITE_BLOCK);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries ->
			entries.addAfter(Items.NETHERITE_BLOCK, ModBlocks.ENDRITE_BLOCK)
		);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
			entries.addAfter(Items.RAW_GOLD, ModItems.RAW_ENDRITE);
			entries.addAfter(Items.NETHERITE_INGOT, ModItems.ENDRITE_INGOT);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries ->
			entries.addAfter(Items.NETHERITE_HOE, ModItems.ENDRITE_SHOVEL, ModItems.ENDRITE_PICKAXE, ModItems.ENDRITE_AXE, ModItems.ENDRITE_HOE)
		);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
			entries.addAfter(Items.NETHERITE_SWORD, ModItems.ENDRITE_SWORD);
			entries.addAfter(Items.NETHERITE_AXE, ModItems.ENDRITE_AXE);
			entries.addAfter(Items.NETHERITE_SPEAR, ModItems.ENDRITE_SPEAR);
			entries.addAfter(Items.NETHERITE_BOOTS, ModItems.ENDRITE_HELMET, ModItems.ENDRITE_CHESTPLATE, ModItems.ENDRITE_LEGGINGS, ModItems.ENDRITE_BOOTS);
		});
	}

	private ModItemGroups() {
	}
}
