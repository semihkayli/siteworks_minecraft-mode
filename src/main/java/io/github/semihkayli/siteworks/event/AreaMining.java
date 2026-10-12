package io.github.semihkayli.siteworks.event;

import io.github.semihkayli.siteworks.item.AreaToolItem;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 3x3 digging for {@link AreaToolItem}s.
 * After the player breaks one block, we break the 8 blocks around it,
 * on the plane the player is looking at (a wall in front, or the floor/ceiling).
 */
public final class AreaMining {
	/** Players who are breaking the extra blocks right now. Stops the event from calling itself again. */
	private static final Set<UUID> BUSY = new HashSet<>();

	private AreaMining() {
	}

	public static void init() {
		PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
			if (player instanceof ServerPlayer serverPlayer) {
				breakArea(level, serverPlayer, pos, state);
			}
		});
	}

	private static void breakArea(Level level, ServerPlayer player, BlockPos center, BlockState centerState) {
		ItemStack tool = player.getMainHandItem();
		if (!(tool.getItem() instanceof AreaToolItem areaTool)) {
			return;
		}
		// Sneak = dig only 1 block. The 3x3 also starts only if the center block is in the tag.
		if (player.isShiftKeyDown() || !centerState.is(areaTool.areaBlocks()) || BUSY.contains(player.getUUID())) {
			return;
		}

		// The 3x3 plane is flat against the face the player hit (top face = a floor, side face = a wall).
		Direction.Axis lookAxis = hitFaceAxis(player, center, centerState);

		BUSY.add(player.getUUID());
		try {
			for (int a = -1; a <= 1; a++) {
				for (int b = -1; b <= 1; b++) {
					if (a == 0 && b == 0) {
						continue;
					}
					BlockPos pos = switch (lookAxis) {
						case X -> center.offset(0, a, b);
						case Y -> center.offset(a, 0, b);
						case Z -> center.offset(a, b, 0);
					};
					BlockState state = level.getBlockState(pos);
					if (!state.is(areaTool.areaBlocks()) || state.getDestroySpeed(level, pos) < 0) {
						continue;
					}
					// destroyBlock() skips this check (only a normal click does it), so we do it ourselves:
					// spawn protection and the world border.
					if (!level.mayInteract(player, pos)) {
						continue;
					}
					// The tool broke (or the player switched items): stop.
					if (player.getMainHandItem() != tool || tool.isEmpty()) {
						return;
					}
					// The vanilla break: drops, 1 durability, enchantments and Fabric events (claim mods) work as normal.
					player.gameMode.destroyBlock(pos);
				}
			}
		} finally {
			BUSY.remove(player.getUUID());
		}
	}

	/**
	 * Which face did the player hit? The block is already broken (air) when this event runs,
	 * so we cast the player's view ray against the OLD block's shape. If that fails (rare),
	 * we fall back to the direction the player looks.
	 */
	private static Direction.Axis hitFaceAxis(ServerPlayer player, BlockPos center, BlockState centerState) {
		Vec3 eye = player.getEyePosition();
		Vec3 end = eye.add(player.getViewVector(1.0f).scale(player.blockInteractionRange() + 1.0));
		BlockHitResult hit = centerState.getShape(EmptyBlockGetter.INSTANCE, center).clip(eye, end, center);
		if (hit != null) {
			return hit.getDirection().getAxis();
		}
		return Direction.orderedByNearest(player)[0].getAxis();
	}
}
