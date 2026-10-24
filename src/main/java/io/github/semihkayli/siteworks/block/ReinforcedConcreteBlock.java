package io.github.semihkayli.siteworks.block;

import io.github.semihkayli.siteworks.foundation.Foundation;
import io.github.semihkayli.siteworks.foundation.FoundationLookup;
import io.github.semihkayli.siteworks.util.Messages;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Reinforced Concrete (betonarme). Very blast resistant, and the material of foundations (Phase 3¾).
 * Right-click it with EMPTY hands: the action bar tells if this stack is a foundation and how high
 * the building on it may be. With something in your hands, it acts like a normal block (you can build on it).
 */
public class ReinforcedConcreteBlock extends Block {
	public ReinforcedConcreteBlock(Properties properties) {
		super(properties);
	}

	/** Holding something: PASS, so the item works as normal (e.g. placing bricks on the foundation). */
	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		return stack.isEmpty() ? InteractionResult.TRY_WITH_EMPTY_HAND : InteractionResult.PASS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		// Only with both hands empty: a block in the other hand should still be placed as normal.
		if (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide()) {
			showInfo(level, pos, player);
		}
		return InteractionResult.SUCCESS;
	}

	private static void showInfo(Level level, BlockPos pos, Player player) {
		Foundation.Stack stack = FoundationLookup.stackAt(level, pos);
		if (stack.isFoundation()) {
			player.sendOverlayMessage(Component.translatable("message.siteworks.foundation.ok",
					stack.layers(), stack.maxHeight(), stack.limitY()).withStyle(ChatFormatting.GREEN));
		} else {
			Messages.error(player, Component.translatable("message.siteworks.foundation.none"));
		}
	}
}
