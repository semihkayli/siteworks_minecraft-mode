package io.github.semihkayli.siteworks.block;

import io.github.semihkayli.siteworks.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.redstone.Orientation;

/**
 * Fresh concrete from a Concrete Bucket. It has gravity like sand, and after {@link #CURE_TICKS}
 * it turns into Concrete (or Reinforced Concrete, if it was poured over rebar).
 *
 * The timer is ONE scheduled tick (a light, built-in "call me again in N ticks"). No block entity.
 * Minecraft keeps only one scheduled tick per block position, so a 2nd "check if I should fall" tick
 * would be ignored while the cure tick waits. That's why we fall DIRECTLY in neighborChanged
 * when the block below is removed, instead of scheduling a tick like vanilla sand does.
 */
public class WetConcreteBlock extends FallingBlock {
	/** True = rebar inside: cures into Reinforced Concrete, and gives the rebar back when broken. */
	public static final BooleanProperty REINFORCED = BooleanProperty.create("reinforced");

	/** 2 minutes (20 ticks = 1 second). */
	public static final int CURE_TICKS = 2400;

	/** A gray color for the small dust particles under a block that's about to fall. */
	private static final int DUST_COLOR = 0x8C8C8C;

	public WetConcreteBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(REINFORCED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(REINFORCED);
	}

	/**
	 * Called when the block appears: poured, landed after a fall, or set by a command.
	 * The timer always starts again here.
	 */
	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!(level instanceof ServerLevel serverLevel) || oldState.is(this)) {
			return;
		}
		// An old timer from wet concrete that fell away from here would block our new one, so remove it first.
		serverLevel.getBlockTicks().clearArea(BoundingBox.fromCorners(pos, pos));
		if (isFree(level.getBlockState(pos.below()))) {
			// No support: fall soon (like vanilla sand placed in the air).
			level.scheduleTick(pos, this, getDelayAfterPlace());
		} else {
			level.scheduleTick(pos, this, CURE_TICKS);
		}
	}

	/** Vanilla schedules a fall check here, but our cure tick would block it. We use neighborChanged instead. */
	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
			Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
		return state;
	}

	/** A neighbor changed. If it was the support below and it's gone, fall now. */
	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, Orientation orientation, boolean movedByPiston) {
		if (level instanceof ServerLevel serverLevel && canFall(serverLevel, pos)) {
			startFalling(serverLevel, pos, state);
		}
	}

	/** The scheduled tick: fall if there's no support, otherwise the 2 minutes are over, so cure. */
	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (canFall(level, pos)) {
			startFalling(level, pos, state);
			return;
		}
		Block cured = state.getValue(REINFORCED) ? ModBlocks.REINFORCED_CONCRETE : ModBlocks.CONCRETE;
		level.setBlockAndUpdate(pos, cured.defaultBlockState());
	}

	private static boolean canFall(ServerLevel level, BlockPos pos) {
		return isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinY();
	}

	private void startFalling(ServerLevel level, BlockPos pos, BlockState state) {
		falling(FallingBlockEntity.fall(level, pos, state));
	}

	/** Falling wet concrete hurts: 1 damage per block fallen, max 10 (5 hearts). The Hard Hat blocks it. */
	@Override
	protected void falling(FallingBlockEntity entity) {
		entity.setHurtsEntities(1.0f, 10);
	}

	/** It landed on something it can't stay on (like a torch). Wet concrete has no item, but the rebar comes back. */
	@Override
	public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity entity) {
		if (entity.getBlockState().getValue(REINFORCED)) {
			Block.popResource(level, pos, new ItemStack(ModBlocks.REBAR));
		}
	}

	@Override
	public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
		return DUST_COLOR;
	}
}
