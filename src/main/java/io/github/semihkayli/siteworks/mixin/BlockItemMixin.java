package io.github.semihkayli.siteworks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.semihkayli.siteworks.foundation.Foundation;
import io.github.semihkayli.siteworks.foundation.FoundationLookup;
import io.github.semihkayli.siteworks.util.Messages;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Foundation rule, the hard stop (Phase 3¾ step 4): a player can't make a building on a foundation taller than
 * its limit. Every normal block placement (by hand, the Trowel, Steel Beams...) goes through BlockItem.place(),
 * which calls placeBlock() at the final spot. We wrap that call: too tall = no block + a red message.
 * It runs on the client too (the client guesses the result), so no "ghost" block appears for a moment.
 */
@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
	@WrapOperation(method = "place", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean siteworks$foundationHardStop(BlockItem item, BlockPlaceContext context, BlockState state,
			Operation<Boolean> original) {
		Player player = context.getPlayer();
		Foundation.Stack limit = FoundationLookup.refusedFor(player, context.getLevel(), context.getClickedPos());
		if (limit != null) {
			if (!context.getLevel().isClientSide()) {
				Messages.error(player, FoundationLookup.tooTallMessage(limit));
			}
			return false;
		}
		return original.call(item, context, state);
	}
}
