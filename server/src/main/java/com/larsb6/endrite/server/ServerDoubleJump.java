package com.larsb6.endrite.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.larsb6.endrite.DoubleJump;
import com.larsb6.endrite.Endrite;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
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
 * Its strength is set in config/endrite.json.
 */
public final class ServerDoubleJump {
	private enum State { GROUNDED, READY, USED }

	/** Contents of config/endrite.json. */
	private static final class Config {
		/** Multiplier on the normal jump speed: 1.0 = +1.25 blocks, 1.5 = +2.6, 1.75 = +3.4, 2.0 = +4.3. */
		double double_jump_strength = 1.75;
	}

	private static final Map<UUID, State> STATES = new HashMap<>();
	/** Height at which each player pressed the double jump; fall damage counts from there. */
	private static final Map<UUID, Double> LAUNCH_Y = new HashMap<>();
	/** Highest point reached after the double jump. */
	private static final Map<UUID, Double> APEX_Y = new HashMap<>();
	/** Consecutive ticks on the ground, used to drop LAUNCH_Y/APEX_Y once a landing caused no fall damage. */
	private static final Map<UUID, Integer> GROUNDED_TICKS = new HashMap<>();
	/**
	 * The server may already consider a player grounded a packet before the landing that applies fall
	 * damage, so the double jump heights are kept for a short while after touching the ground.
	 */
	private static final int KEEP_AFTER_LANDING_TICKS = 10;
	private static double strength;

	static void register() {
		strength = loadConfig().double_jump_strength;
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				tick(player);
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			STATES.remove(handler.player.getUuid());
			forgetJump(handler.player.getUuid());
		});
	}

	private static void tick(ServerPlayerEntity player) {
		if (player.isOnGround() || player.isClimbing() || player.isTouchingWater() || player.hasVehicle()
			|| player.getAbilities().flying || player.isGliding()) {
			STATES.remove(player.getUuid());
			if (GROUNDED_TICKS.merge(player.getUuid(), 1, Integer::sum) >= KEEP_AFTER_LANDING_TICKS) {
				forgetJump(player.getUuid());
			}
			return;
		}

		GROUNDED_TICKS.remove(player.getUuid());
		APEX_Y.computeIfPresent(player.getUuid(), (uuid, apex) -> Math.max(apex, player.getY()));

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
		// with a (configurably stronger) normal jump, like LivingEntity#jump does.
		double jumpVelocity = player.getAttributeValue(EntityAttributes.JUMP_STRENGTH) * strength + player.getJumpBoostVelocityModifier();
		Vec3d movement = player.getMovement();
		player.setVelocity(movement.x, jumpVelocity, movement.z);
		player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
		player.onLanding();
		LAUNCH_Y.put(player.getUuid(), player.getY());
		APEX_Y.put(player.getUuid(), player.getY());

		ServerWorld world = player.getEntityWorld();
		world.spawnParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY(), player.getZ(), 20, 0.3, 0.05, 0.3, 0.05);
		world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.4F, 1.6F);
	}

	/**
	 * Fall damage after a double jump counts from the height where it was pressed, so the extra height
	 * of a strong double jump never hurts on its own. The real fall distance is left untouched, because
	 * other mechanics such as the mace smash depend on it; only the damage calculation is adjusted.
	 */
	public static double adjustFallDistance(ServerPlayerEntity player, double fallDistance) {
		Double launchY = LAUNCH_Y.get(player.getUuid());
		Double apexY = APEX_Y.get(player.getUuid());
		if (launchY == null || apexY == null) {
			return fallDistance;
		}
		forgetJump(player.getUuid());
		return Math.max(0.0, fallDistance - Math.max(0.0, apexY - launchY));
	}

	private static void forgetJump(UUID uuid) {
		LAUNCH_Y.remove(uuid);
		APEX_Y.remove(uuid);
		GROUNDED_TICKS.remove(uuid);
	}

	private static Config loadConfig() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Path path = FabricLoader.getInstance().getConfigDir().resolve("endrite.json");
		Config config = new Config();
		try {
			if (Files.exists(path)) {
				Config loaded = gson.fromJson(Files.readString(path), Config.class);
				if (loaded != null) {
					config = loaded;
				}
			}
			Files.writeString(path, gson.toJson(config));
		} catch (IOException | RuntimeException e) {
			Endrite.LOGGER.warn("Could not read {}, using defaults: {}", path, e.toString());
		}
		return config;
	}

	private ServerDoubleJump() {
	}
}
