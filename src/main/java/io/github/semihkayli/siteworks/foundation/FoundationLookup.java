package io.github.semihkayli.siteworks.foundation;

import io.github.semihkayli.siteworks.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** The Foundation rule on the real world: turns real blocks into {@link Foundation.Cell}s, one column at a time. */
public final class FoundationLookup {
	private FoundationLookup() {
	}

	/** The stack of Reinforced Concrete at {@code pos} (which must be Reinforced Concrete). */
	public static Foundation.Stack stackAt(Level level, BlockPos pos) {
		return Foundation.stackAt(column(level, pos), pos.getY(), level.getMinY(), level.getMaxY());
	}

	/** The foundation that carries a block at {@code pos}, or null if there is none under it. */
	public static Foundation.Stack below(Level level, BlockPos pos) {
		return Foundation.below(column(level, pos), pos.getY(), level.getMinY());
	}

	private static Foundation.Column column(Level level, BlockPos pos) {
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
		return y -> cellAt(level, cursor.setY(y));
	}

	/**
	 * What a real block is for the rule. Solid = a block with a full top (you could stand concrete on it):
	 * stone, dirt, grass, sand, gravel, bricks... (user, 2026-10-24: any ground is OK, digging to stone is boring).
	 */
	static Foundation.Cell cellAt(Level level, BlockPos pos) {
		if (level.isOutsideBuildHeight(pos)) {
			return Foundation.Cell.OTHER;
		}
		BlockState state = level.getBlockState(pos);
		if (state.is(ModBlocks.REINFORCED_CONCRETE)) {
			return Foundation.Cell.FOUNDATION;
		}
		return state.isFaceSturdy(level, pos, Direction.UP) ? Foundation.Cell.SOLID : Foundation.Cell.OTHER;
	}
}
