package io.github.semihkayli.siteworks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A thin net at the TOP of its block space (like a trapdoor in the "up" position).
 * Players and mobs that land on it take no fall damage and bounce a little.
 *
 * Why at the top? Vanilla finds the landing block 0.2 blocks under the feet (for fallOn)
 * and 0.5 blocks under the feet (for the bounce). A net on the floor of its block is too thin,
 * so vanilla would use the block below it. At the top, both checks find the net.
 */
public class SafetyNetBlock extends Block {
	private static final VoxelShape SHAPE = Block.box(0, 13, 0, 16, 16, 16);

	public SafetyNetBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
		// No fall damage at all. We don't call super.fallOn(), which would hurt the entity.
		// The small bounce comes from bounceRestitution in ModBlocks (vanilla handles it).
	}
}
