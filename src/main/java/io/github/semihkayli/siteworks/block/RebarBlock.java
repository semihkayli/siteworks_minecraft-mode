package io.github.semihkayli.siteworks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A small cage of 4 steel bars. It floats (needs no support), like iron bars.
 * Place it inside a mold before pouring: the cell then cures into Reinforced Concrete (Phase 2, step 4).
 */
public class RebarBlock extends Block {
	// One box around the 4 bars (pixels 3-13), so it's easy to aim at and can't be walked through.
	private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 16, 13);

	public RebarBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
}
