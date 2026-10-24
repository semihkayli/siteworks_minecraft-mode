package io.github.semihkayli.siteworks.foundation;

import io.github.semihkayli.siteworks.registry.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
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

	/** Is the block at {@code pos} part of a building carried by a foundation (= protected from explosions and the Wither)? */
	public static boolean isCarried(Level level, BlockPos pos) {
		Foundation.Stack stack = below(level, pos);
		return stack != null && stack.carries(pos.getY());
	}

	/**
	 * The hard stop for a PLAYER placing a block at {@code pos}: the foundation whose limit it would break,
	 * or null = OK. Creative mode ignores the limit, and so does placing without a player (dispensers...).
	 */
	public static Foundation.Stack refusedFor(Player player, Level level, BlockPos pos) {
		if (player == null || player.getAbilities().instabuild) {
			return null;
		}
		boolean blockBelow = !level.getBlockState(pos.below()).isAir();
		return Foundation.tooTall(column(level, pos), pos.getY(), blockBelow, level.getMinY());
	}

	/** "Foundation too thin for this height! Add a layer (max y 71)." */
	public static Component tooTallMessage(Foundation.Stack stack) {
		return Component.translatable("message.siteworks.foundation.too_tall", stack.limitY());
	}

	/** Removes every carried (protected) block from an explosion's list of blocks to destroy. */
	public static void removeCarried(Level level, List<BlockPos> positions) {
		if (positions.isEmpty()) {
			return;
		}
		List<Foundation.Pos> asPos = new ArrayList<>(positions.size());
		for (BlockPos pos : positions) {
			asPos.add(new Foundation.Pos(pos.getX(), pos.getY(), pos.getZ()));
		}
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		Set<Foundation.Pos> carried = Foundation.carried((x, y, z) -> cellAt(level, cursor.set(x, y, z)), asPos, level.getMinY());
		if (!carried.isEmpty()) {
			positions.removeIf(pos -> carried.contains(new Foundation.Pos(pos.getX(), pos.getY(), pos.getZ())));
		}
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
