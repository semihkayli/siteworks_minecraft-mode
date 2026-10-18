package io.github.semihkayli.siteworks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Steel Beam item. Right-click a beam with it, and the new beam goes to the END of that beam line
 * (up to 8 blocks ahead), like vanilla scaffolding. So you can build an 8-long bridge from safe ground.
 * Sneak = normal placement (next to the face you click).
 *
 * How: BlockItem.place() asks updatePlacementContext() where to place. We return a context at the end
 * of the line, and vanilla does the rest (entity check, sound, using up the item, creative mode...).
 */
public class SteelBeamItem extends BlockItem {
	/** How far ahead the end of the line can be: 8 = the 8th block from the beam you click. */
	public static final int MAX_REACH = 8;

	public SteelBeamItem(Block block, Properties properties) {
		super(block, properties);
	}

	/** Returns where to place, or null = place nothing. */
	@Override
	public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
		Level level = context.getLevel();
		// getClickedPos() is the spot in FRONT of the face, unless the clicked block can be replaced (a beam can't).
		BlockPos clicked = context.replacingClickedOnBlock()
				? context.getClickedPos()
				: context.getClickedPos().relative(context.getClickedFace().getOpposite());
		BlockState clickedState = level.getBlockState(clicked);
		if (context.isSecondaryUseActive() || !clickedState.is(getBlock())) {
			return context;
		}

		Direction.Axis axis = clickedState.getValue(RotatedPillarBlock.AXIS);
		Direction direction = extendDirection(context, axis);
		Player player = context.getPlayer();
		BlockPos.MutableBlockPos pos = clicked.mutable();
		for (int i = 1; i <= MAX_REACH; i++) {
			pos.move(direction);
			BlockState state = level.getBlockState(pos);
			if (state.is(getBlock()) && state.getValue(RotatedPillarBlock.AXIS) == axis) {
				continue; // still the same beam line, look further
			}
			if (!level.isInWorldBounds(pos)) {
				// Too high or too low: vanilla's "Height limit for building is ..." message, like scaffolding.
				if (player instanceof ServerPlayer serverPlayer && pos.getY() > level.getMaxY()) {
					serverPlayer.sendBuildLimitMessage(true, level.getMaxY());
				}
				return null;
			}
			// Something solid is in the way, or the spot is protected (spawn protection, world border).
			if (!state.canBeReplaced(context) || (player != null && !level.mayInteract(player, pos))) {
				message(context, "message.siteworks.beam.blocked");
				return null;
			}
			// The clicked face decides the new beam's axis, so we "click" the new spot from the extend direction.
			return BlockPlaceContext.at(context, pos.immutable(), direction);
		}
		message(context, "message.siteworks.beam.too_far");
		return null;
	}

	/**
	 * Which way along the beam to build:
	 * - you clicked an end of the beam -> out of that end;
	 * - you clicked the side of a standing beam -> up (like scaffolding);
	 * - you clicked the side or top of a lying beam -> the way you are looking along it.
	 */
	private static Direction extendDirection(BlockPlaceContext context, Direction.Axis axis) {
		Direction face = context.getClickedFace();
		if (face.getAxis() == axis) {
			return face;
		}
		if (axis == Direction.Axis.Y) {
			return Direction.UP;
		}
		// Sorted from the direction you look at most to the least. Take the first one along the beam.
		for (Direction look : context.getNearestLookingDirections()) {
			if (look.getAxis() == axis) {
				return look;
			}
		}
		return Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
	}

	/** An action bar message, only on the server (the client runs this method too, to guess the result). */
	private static void message(BlockPlaceContext context, String key) {
		if (!context.getLevel().isClientSide() && context.getPlayer() != null) {
			context.getPlayer().sendOverlayMessage(Component.translatable(key).withStyle(ChatFormatting.RED));
		}
	}
}
