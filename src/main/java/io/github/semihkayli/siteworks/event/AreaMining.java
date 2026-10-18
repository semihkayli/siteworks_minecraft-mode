package io.github.semihkayli.siteworks.event;

import io.github.semihkayli.siteworks.item.AreaToolItem;
import io.github.semihkayli.siteworks.registry.ModItems;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
 * Sledgehammer + ceiling = a little debris damage, unless you wear a Hard Hat.
 */
public final class AreaMining {
	/** Players who are breaking the extra blocks right now. Stops the event from calling itself again. */
	private static final Set<UUID> BUSY = new HashSet<>();
	/** Sledgehammer ceiling debris: 2 damage = 1 heart (was ½ heart; the user wanted more, 2026-10-18). */
	private static final float CEILING_DEBRIS_DAMAGE = 2.0f;

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
		Direction hitFace = hitFace(player, center, centerState);
		Direction.Axis lookAxis = hitFace.getAxis();

		int broken = 0;
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
					if (player.gameMode.destroyBlock(pos)) {
						broken++;
					}
				}
			}
		} finally {
			BUSY.remove(player.getUUID());
		}

		// Sledgehammer + a 3x3 CEILING (you hit the bottom face of a block above you) = debris falls on your head.
		if (broken > 0 && hitFace == Direction.DOWN && tool.is(ModItems.SLEDGEHAMMER)) {
			dropCeilingDebris(player, center, centerState);
		}
	}

	/**
	 * Small falling dust from the broken block + 1 heart of "falling block" damage.
	 * It's the same damage type as falling anvils, so the Hard Hat blocks it (HardHatProtection).
	 */
	private static void dropCeilingDebris(ServerPlayer player, BlockPos center, BlockState centerState) {
		ServerLevel level = player.level();
		level.sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, centerState),
				center.getX() + 0.5, center.getY(), center.getZ() + 0.5, 12, 0.8, 0.1, 0.8, 0.0);
		player.hurtServer(level, level.damageSources().fallingBlock(null), CEILING_DEBRIS_DAMAGE);
	}

	/**
	 * Which face did the player hit? The block is already broken (air) when this event runs,
	 * so we cast the player's view ray against the OLD block's shape. If that fails (rare),
	 * we fall back to the direction the player looks.
	 */
	private static Direction hitFace(ServerPlayer player, BlockPos center, BlockState centerState) {
		Vec3 eye = player.getEyePosition();
		Vec3 end = eye.add(player.getViewVector(1.0f).scale(player.blockInteractionRange() + 1.0));
		BlockHitResult hit = centerState.getShape(EmptyBlockGetter.INSTANCE, center).clip(eye, end, center);
		if (hit != null) {
			return hit.getDirection();
		}
		// The face we hit points back at us: looking UP = we hit the DOWN face.
		return Direction.orderedByNearest(player)[0].getOpposite();
	}
}
