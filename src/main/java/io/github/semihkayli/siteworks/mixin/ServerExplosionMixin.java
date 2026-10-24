package io.github.semihkayli.siteworks.mixin;

import io.github.semihkayli.siteworks.foundation.FoundationLookup;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Foundation rule (Phase 3¾ step 3): explosions (TNT, creepers, ghast fireballs, beds...) don't break the blocks
 * of a building that stands on a foundation. Players and mobs still get hurt as normal.
 *
 * A mixin adds our code into one of Minecraft's own methods when the game starts (Fabric has no explosion event).
 * Vanilla first makes a list of the blocks an explosion will break (calculateExplodedPositions), then breaks them
 * and lights fires there. We take the protected blocks out of that list just before vanilla returns it.
 */
@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin {
	@Shadow
	@Final
	private ServerLevel level;

	@Inject(method = "calculateExplodedPositions", at = @At("RETURN"))
	private void siteworks$keepFoundationBuildings(CallbackInfoReturnable<List<BlockPos>> cir) {
		FoundationLookup.removeProtected(level, cir.getReturnValue());
	}
}
