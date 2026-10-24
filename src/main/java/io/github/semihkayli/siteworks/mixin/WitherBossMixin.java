package io.github.semihkayli.siteworks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.semihkayli.siteworks.foundation.FoundationLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Foundation rule (Phase 3¾ step 3, user's choice): the Wither can't break the blocks of a building that stands
 * on a foundation by touching them either. (Its skulls explode, and ServerExplosionMixin handles those.)
 *
 * When the Wither is hurt, vanilla breaks the blocks around it with level.destroyBlock(...) in customServerAiStep.
 * We wrap that one call: protected block = skip it, everything else = vanilla as normal.
 */
@Mixin(WitherBoss.class)
public abstract class WitherBossMixin {
	@WrapOperation(method = "customServerAiStep", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
	private boolean siteworks$keepFoundationBuildings(ServerLevel level, BlockPos pos, boolean drop, Entity breaker,
			Operation<Boolean> original) {
		if (FoundationLookup.isProtected(level, pos)) {
			return false;
		}
		return original.call(level, pos, drop, breaker);
	}
}
