package io.github.semihkayli.siteworks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A steel I-beam (I-profil). It has an axis like a log (RotatedPillarBlock): it points along the face you click.
 * The shape is 2 flanges (3 px plates) + a web (4 px) in the middle. Lying beams have the flanges on top and
 * bottom, so you can walk on them. The shapes match the model rotations in the blockstate.
 */
public class SteelBeamBlock extends RotatedPillarBlock {
	// Standing (axis Y): the flanges are the north and south sides, the web goes north-south between them.
	private static final VoxelShape SHAPE_Y = Shapes.or(
			Block.box(0, 0, 0, 16, 16, 3),
			Block.box(0, 0, 13, 16, 16, 16),
			Block.box(6, 0, 3, 10, 16, 13));
	// Lying along Z: flanges at the bottom and the top, a standing web in the middle.
	private static final VoxelShape SHAPE_Z = Shapes.or(
			Block.box(0, 0, 0, 16, 3, 16),
			Block.box(0, 13, 0, 16, 16, 16),
			Block.box(6, 3, 0, 10, 13, 16));
	// Lying along X: the same, turned 90 degrees.
	private static final VoxelShape SHAPE_X = Shapes.or(
			Block.box(0, 0, 0, 16, 3, 16),
			Block.box(0, 13, 0, 16, 16, 16),
			Block.box(0, 3, 6, 16, 13, 10));

	public SteelBeamBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(AXIS)) {
			case X -> SHAPE_X;
			case Y -> SHAPE_Y;
			case Z -> SHAPE_Z;
		};
	}
}
