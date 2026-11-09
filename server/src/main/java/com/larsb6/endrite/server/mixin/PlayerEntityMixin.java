package com.larsb6.endrite.server.mixin;

import com.larsb6.endrite.server.ServerDoubleJump;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin {
	@ModifyVariable(method = "handleFallDamage", at = @At("HEAD"), argsOnly = true)
	private double endrite$countFallFromDoubleJump(double fallDistance) {
		if ((Object) this instanceof ServerPlayerEntity player) {
			return ServerDoubleJump.adjustFallDistance(player, fallDistance);
		}
		return fallDistance;
	}
}
