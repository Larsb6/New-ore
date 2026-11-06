package com.larsb6.endrite.server;

import com.larsb6.endrite.Endrite;
import com.larsb6.endrite.ModBlocks;
import com.larsb6.endrite.ModItems;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import xyz.nucleoid.packettweaker.PacketContext;

/**
 * Tells Polymer how vanilla clients should see each Endrite block and item. Blocks become
 * resource-pack textured block states, items become vanilla items that keep all their components
 * (stats, durability, armor model) and use the Endrite item models from the resource pack.
 */
final class PolymerOverlays {
	static void register() {
		texturedBlock(ModBlocks.ENDRITE_ORE);
		texturedBlock(ModBlocks.RAW_ENDRITE_BLOCK);
		texturedBlock(ModBlocks.ENDRITE_BLOCK);

		item(ModItems.RAW_ENDRITE, Items.RAW_GOLD);
		item(ModItems.ENDRITE_INGOT, Items.NETHERITE_INGOT);
		item(ModItems.ENDRITE_SWORD, Items.NETHERITE_SWORD);
		item(ModItems.ENDRITE_PICKAXE, Items.NETHERITE_PICKAXE);
		item(ModItems.ENDRITE_AXE, Items.NETHERITE_AXE);
		item(ModItems.ENDRITE_SHOVEL, Items.NETHERITE_SHOVEL);
		item(ModItems.ENDRITE_HOE, Items.NETHERITE_HOE);
		item(ModItems.ENDRITE_SPEAR, Items.NETHERITE_SPEAR);
		item(ModItems.ENDRITE_HELMET, Items.NETHERITE_HELMET);
		item(ModItems.ENDRITE_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
		item(ModItems.ENDRITE_LEGGINGS, Items.NETHERITE_LEGGINGS);
		item(ModItems.ENDRITE_BOOTS, Items.NETHERITE_BOOTS);
	}

	private static void texturedBlock(Block block) {
		BlockState clientState = PolymerBlockResourceUtils.requestBlock(
			BlockModelType.FULL_BLOCK, PolymerBlockModel.of(Endrite.id("block/" + Registries.BLOCK.getId(block).getPath()))
		);
		PolymerBlock.registerOverlay(block, new TexturedBlock(clientState));
		PolymerItem.registerOverlay(block.asItem(), new BlockItemOverlay());
	}

	private static void item(Item item, Item clientItem) {
		PolymerItem.registerOverlay(item, (stack, context) -> clientItem);
	}

	private record TexturedBlock(BlockState clientState) implements PolymerTexturedBlock {
		@Override
		public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
			return clientState;
		}
	}

	/** Same behaviour as Polymer's PolymerBlockItem: placement is handled by the server. */
	private static final class BlockItemOverlay implements PolymerItem {
		@Override
		public Item getPolymerItem(ItemStack stack, PacketContext context) {
			return Items.TRIAL_KEY;
		}

		@Override
		public boolean isPolymerBlockInteraction(BlockState state, ServerPlayerEntity player, Hand hand, ItemStack stack, ServerWorld world, BlockHitResult hit, ActionResult result) {
			return true;
		}

		@Override
		public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayerEntity player, Hand hand, ItemStack stack, ServerWorld world, BlockHitResult hit) {
			return true;
		}
	}

	private PolymerOverlays() {
	}
}
