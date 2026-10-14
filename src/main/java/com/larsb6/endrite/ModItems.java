package com.larsb6.endrite;

import java.util.function.Function;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import static com.larsb6.endrite.ModMaterials.ENDRITE_ARMOR;
import static com.larsb6.endrite.ModMaterials.ENDRITE_TOOL;

public final class ModItems {
	public static final Item RAW_ENDRITE = register("raw_endrite", Item::new, new Item.Settings());
	public static final Item ENDRITE_INGOT = register("endrite_ingot", Item::new, new Item.Settings().fireproof());

	// Same attack damage/speed modifiers as the vanilla diamond/netherite tools; the material adds +1 over netherite.
	public static final Item ENDRITE_SWORD = register("endrite_sword", Item::new, new Item.Settings().sword(ENDRITE_TOOL, 3.0F, -2.4F).fireproof());
	public static final Item ENDRITE_SHOVEL = register(
		"endrite_shovel", settings -> new ShovelItem(ENDRITE_TOOL, 1.5F, -3.0F, settings), new Item.Settings().fireproof()
	);
	public static final Item ENDRITE_PICKAXE = register("endrite_pickaxe", Item::new, new Item.Settings().pickaxe(ENDRITE_TOOL, 1.0F, -2.8F).fireproof());
	public static final Item ENDRITE_AXE = register(
		"endrite_axe", settings -> new AxeItem(ENDRITE_TOOL, 5.0F, -3.0F, settings), new Item.Settings().fireproof()
	);
	public static final Item ENDRITE_HOE = register(
		"endrite_hoe", settings -> new HoeItem(ENDRITE_TOOL, -5.0F, 0.0F, settings), new Item.Settings().fireproof()
	);
	// Spear values continue the diamond -> netherite progression one more step.
	public static final Item ENDRITE_SPEAR = register(
		"endrite_spear",
		Item::new,
		new Item.Settings().spear(ENDRITE_TOOL, 1.25F, 1.325F, 0.3F, 2.0F, 6.5F, 4.5F, 5.1F, 7.5F, 4.6F).fireproof()
	);

	public static final Item ENDRITE_HELMET = register(
		"endrite_helmet", Item::new, new Item.Settings().armor(ENDRITE_ARMOR, EquipmentType.HELMET).fireproof()
	);
	public static final Item ENDRITE_CHESTPLATE = register(
		"endrite_chestplate", Item::new, new Item.Settings().armor(ENDRITE_ARMOR, EquipmentType.CHESTPLATE).fireproof()
	);
	public static final Item ENDRITE_LEGGINGS = register(
		"endrite_leggings", Item::new, new Item.Settings().armor(ENDRITE_ARMOR, EquipmentType.LEGGINGS).fireproof()
	);
	public static final Item ENDRITE_BOOTS = register(
		"endrite_boots", Item::new, new Item.Settings().armor(ENDRITE_ARMOR, EquipmentType.BOOTS).fireproof()
	);

	private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Endrite.id(name));
		return Registry.register(Registries.ITEM, key, factory.apply(settings.registryKey(key)));
	}

	static void initialize() {
	}

	private ModItems() {
	}
}
