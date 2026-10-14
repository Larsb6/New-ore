package com.larsb6.endrite.client;

import com.larsb6.endrite.DoubleJump;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class EndriteClient implements ClientModInitializer {
	/** Whether the double jump is still available in the current airtime. */
	private boolean jumpAvailable;
	/** The jump key must be released after leaving the ground, so holding it does not trigger instantly. */
	private boolean jumpReleased;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
	}

	private void tick(MinecraftClient client) {
		ClientPlayerEntity player = client.player;
		if (player == null) {
			return;
		}

		if (player.isOnGround() || player.isClimbing() || player.isTouchingWater() || player.hasVehicle()) {
			jumpAvailable = true;
			jumpReleased = false;
			return;
		}

		boolean jumpPressed = client.options.jumpKey.isPressed();
		if (!jumpPressed) {
			jumpReleased = true;
			return;
		}

		if (jumpReleased && jumpAvailable && DoubleJump.canDoubleJump(player) && ClientPlayNetworking.canSend(DoubleJump.Payload.ID)) {
			jumpAvailable = false;
			jumpReleased = false;
			player.jump();
			ClientPlayNetworking.send(new DoubleJump.Payload());
		}
	}
}
