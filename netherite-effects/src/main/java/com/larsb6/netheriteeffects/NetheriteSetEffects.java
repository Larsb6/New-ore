package com.larsb6.netheriteeffects;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Wearing a full netherite armor set gives infinite Fire Resistance and Resistance I. The effects are
 * checked every tick, so they appear and disappear the moment the set is completed or broken.
 * Everything runs on the server, so players need no mod.
 */
public class NetheriteSetEffects implements ModInitializer {
	private static final List<RegistryEntry<StatusEffect>> EFFECTS = List.of(StatusEffects.FIRE_RESISTANCE, StatusEffects.RESISTANCE);

	/** Players that wore the full set last tick. */
	private static final Set<UUID> WEARING = new HashSet<>();

	@Override
	public void onInitialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				update(player);
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> WEARING.remove(handler.player.getUuid()));
	}

	private static void update(ServerPlayerEntity player) {
		boolean fullSet = player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.NETHERITE_HELMET)
			&& player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.NETHERITE_CHESTPLATE)
			&& player.getEquippedStack(EquipmentSlot.LEGS).isOf(Items.NETHERITE_LEGGINGS)
			&& player.getEquippedStack(EquipmentSlot.FEET).isOf(Items.NETHERITE_BOOTS);
		boolean wasWearing = fullSet ? !WEARING.add(player.getUuid()) : WEARING.remove(player.getUuid());

		for (RegistryEntry<StatusEffect> effect : EFFECTS) {
			StatusEffectInstance current = player.getStatusEffect(effect);
			if (fullSet) {
				// Only fill in when the effect is missing: a potion or beacon effect keeps running, and
				// once it ends the set effect takes over on the next tick.
				if (current == null) {
					// Amplifier 0 = level I; ambient with no particles, like a beacon effect
					player.addStatusEffect(new StatusEffectInstance(effect, StatusEffectInstance.INFINITE, 0, true, false, true));
				}
			} else if (current != null && isFromSet(current)) {
				player.removeStatusEffect(effect);
			} else if (current != null && wasWearing) {
				// A stronger effect (e.g. a beacon's Resistance II) applied over the set effect keeps the
				// set effect hidden underneath and would bring it back when it ends. Re-apply it alone.
				player.removeStatusEffect(effect);
				player.addStatusEffect(new StatusEffectInstance(effect, current.getDuration(), current.getAmplifier(),
					current.isAmbient(), current.shouldShowParticles(), current.shouldShowIcon()));
			}
		}
	}

	private static boolean isFromSet(StatusEffectInstance effect) {
		return effect.isInfinite() && effect.getAmplifier() == 0 && effect.isAmbient();
	}
}
