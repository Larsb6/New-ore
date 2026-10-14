package com.larsb6.endrite;

import java.util.Map;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvents;

public final class ModMaterials {
	/**
	 * One step above netherite, using the same step size as diamond -> netherite
	 * (diamond 1561 / 8.0 / +3, netherite 2031 / 9.0 / +4).
	 */
	public static final ToolMaterial ENDRITE_TOOL = new ToolMaterial(
		ModTags.INCORRECT_FOR_ENDRITE_TOOL, 2501, 10.0F, 5.0F, 15, ModTags.ENDRITE_TOOL_MATERIALS
	);

	public static final RegistryKey<EquipmentAsset> ENDRITE_ASSET = RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Endrite.id("endrite"));

	/** Same armor points as netherite, but more durability (37 -> 42) and toughness (3.0 -> 4.0). */
	public static final ArmorMaterial ENDRITE_ARMOR = new ArmorMaterial(
		42,
		Map.of(
			EquipmentType.HELMET, 3,
			EquipmentType.CHESTPLATE, 8,
			EquipmentType.LEGGINGS, 6,
			EquipmentType.BOOTS, 3,
			EquipmentType.BODY, 19
		),
		15,
		SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
		4.0F,
		0.1F,
		ModTags.REPAIRS_ENDRITE_ARMOR,
		ENDRITE_ASSET
	);

	private ModMaterials() {
	}
}
