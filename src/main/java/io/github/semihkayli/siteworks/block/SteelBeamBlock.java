package io.github.semihkayli.siteworks.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A steel I-beam (I-profil). It has an axis like a log (RotatedPillarBlock): it points along the face you click.
 * - Lying (axis X or Z): a full-width I-beam. Flanges on top and bottom, so you can walk on it.
 * - Standing (axis Y) = a column: a SLIM I-beam at one side of the block (FACING), like a ladder on a wall.
 *   The free space in front is big enough for a player, and the block is #minecraft:climbable,
 *   so you climb up INSIDE the column's block space, like a ladder (Phase 3 step 2b).
 */
public class SteelBeamBlock extends RotatedPillarBlock {
	/** Standing beams only: the side of the block the column stands on. Lying beams ignore it. */
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

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
	// Standing, facing north: a slim I (seen from above) at the north side, 6 pixels deep.
	// The other 10 pixels (0.625 blocks) are free, and a player is 0.6 wide, so you fit in front of it.
	// rotateHorizontal() turns it for east, south and west, like vanilla ladders.
	private static final Map<Direction, VoxelShape> SHAPES_STANDING = Shapes.rotateHorizontal(Shapes.or(
			Block.box(2, 0, 0, 14, 16, 2),
			Block.box(7, 0, 2, 9, 16, 4),
			Block.box(2, 0, 4, 14, 16, 6)));

	public SteelBeamBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.Y).setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	/**
	 * The axis comes from the clicked face (vanilla). A standing beam stands at the FAR side of the block
	 * (the way you look), so you can walk straight into it. On top of / under another column it copies
	 * that column's side, so a column (also an extended one) stays one straight line.
	 */
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		if (state.getValue(AXIS) != Direction.Axis.Y) {
			return state;
		}
		BlockPos pos = context.getClickedPos();
		for (BlockPos neighbor : new BlockPos[] {pos.below(), pos.above()}) {
			BlockState other = context.getLevel().getBlockState(neighbor);
			if (other.is(this) && other.getValue(AXIS) == Direction.Axis.Y) {
				return state.setValue(FACING, other.getValue(FACING));
			}
		}
		return state.setValue(FACING, context.getHorizontalDirection());
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(AXIS)) {
			case X -> SHAPE_X;
			case Y -> SHAPES_STANDING.get(state.getValue(FACING));
			case Z -> SHAPE_Z;
		};
	}

	// Structures and blueprints can turn blocks: turn the facing too (the axis is turned by RotatedPillarBlock).
	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return super.rotate(state, rotation).setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
	}
}
