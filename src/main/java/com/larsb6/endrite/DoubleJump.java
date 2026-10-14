package com.larsb6.endrite;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

/**
 * Wearing the full Endrite armor set allows one extra jump in mid-air.
 * The client detects the key press and performs the jump; the server validates it
 * and resets the fall distance so the fall damage is measured from the second jump.
 */
public final class DoubleJump {
	/** Players that used their double jump and have not touched the ground since. */
	private static final Set<UUID> USED = new HashSet<>();

	public record Payload() implements CustomPayload {
		public static final CustomPayload.Id<Payload> ID = new CustomPayload.Id<>(Endrite.id("double_jump"));
		public static final PacketCodec<RegistryByteBuf, Payload> CODEC = PacketCodec.unit(new Payload());

		@Override
		public CustomPayload.Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	public static boolean hasFullSet(PlayerEntity player) {
		return player.getEquippedStack(EquipmentSlot.HEAD).isOf(ModItems.ENDRITE_HELMET)
			&& player.getEquippedStack(EquipmentSlot.CHEST).isOf(ModItems.ENDRITE_CHESTPLATE)
			&& player.getEquippedStack(EquipmentSlot.LEGS).isOf(ModItems.ENDRITE_LEGGINGS)
			&& player.getEquippedStack(EquipmentSlot.FEET).isOf(ModItems.ENDRITE_BOOTS);
	}

	/** Shared by client and server: situations where a mid-air jump makes no sense. */
	public static boolean canDoubleJump(PlayerEntity player) {
		return !player.isOnGround()
			&& !player.getAbilities().flying
			&& !player.isGliding()
			&& !player.hasVehicle()
			&& !player.isTouchingWater()
			&& !player.isInLava()
			&& !player.isClimbing()
			&& hasFullSet(player);
	}

	static void initialize() {
		PayloadTypeRegistry.playC2S().register(Payload.ID, Payload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(Payload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			if (!canDoubleJump(player) || !USED.add(player.getUuid())) {
				return;
			}

			player.onLanding();
			ServerWorld world = player.getEntityWorld();
			world.spawnParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY(), player.getZ(), 20, 0.3, 0.05, 0.3, 0.05);
			world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.4F, 1.6F);
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (USED.isEmpty()) {
				return;
			}
			USED.removeIf(uuid -> {
				ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
				return player == null || player.isOnGround() || player.isTouchingWater() || player.isClimbing() || player.hasVehicle();
			});
		});
	}

	private DoubleJump() {
	}
}
