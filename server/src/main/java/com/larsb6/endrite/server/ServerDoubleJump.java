package com.larsb6.endrite.server;

import com.larsb6.endrite.DoubleJump;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

/**
 * Double jump for vanilla clients: the server reads the jump key from the player's input packets
 * and launches the player by sending them a new velocity. Same rules as the client mod: full set,
 * jump key released and pressed again in mid-air, once per airtime, fall distance reset.
 */
final class ServerDoubleJump {
	private enum State { GROUNDED, READY, USED }

	private static final Map<UUID, State> STATES = new HashMap<>();

	static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				tick(player);
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> STATES.remove(handler.player.getUuid()));
	}

	private static void tick(ServerPlayerEntity player) {
		if (player.isOnGround() || player.isClimbing() || player.isTouchingWater() || player.hasVehicle()
			|| player.getAbilities().flying || player.isGliding()) {
			STATES.remove(player.getUuid());
			return;
		}

		boolean jumpPressed = player.getPlayerInput().jump();
		State state = STATES.getOrDefault(player.getUuid(), State.GROUNDED);
		if (!jumpPressed) {
			if (state == State.GROUNDED) {
				STATES.put(player.getUuid(), State.READY);
			}
		} else if (state == State.READY && DoubleJump.canDoubleJump(player)) {
			STATES.put(player.getUuid(), State.USED);
			launch(player);
		}
	}

	private static void launch(ServerPlayerEntity player) {
		// Keep the horizontal momentum the client reported this tick and replace the vertical speed
		// with a normal jump, like LivingEntity#jump does.
		double jumpVelocity = player.getAttributeValue(EntityAttributes.JUMP_STRENGTH) + player.getJumpBoostVelocityModifier();
		Vec3d movement = player.getMovement();
		player.setVelocity(movement.x, jumpVelocity, movement.z);
		player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
		player.onLanding();

		ServerWorld world = player.getEntityWorld();
		world.spawnParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY(), player.getZ(), 20, 0.3, 0.05, 0.3, 0.05);
		world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.4F, 1.6F);
	}

	private ServerDoubleJump() {
	}
}
