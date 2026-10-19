package io.github.semihkayli.siteworks.client;

import io.github.semihkayli.siteworks.item.TrowelItem;
import io.github.semihkayli.siteworks.item.TrowelMode;
import io.github.semihkayli.siteworks.registry.ModDataComponents;
import io.github.semihkayli.siteworks.registry.ModItems;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * The Trowel's placement preview (Phase 3 step 5), client only. While you hold the trowel, small colored dust
 * particles show where bricks will go. Only YOU see them (they are made on your computer, not on the server).
 * - No row start yet: a WHITE dot on the spot you look at (= the start of your row).
 * - Row start set: a YELLOW dot on the start, GREEN dots on the row from the start to the spot you look at
 *   (only the spots a brick can really go, like the real placing), or a RED dot if that spot is not
 *   in a straight line or too far (then a click makes it the new start).
 */
public final class TrowelPreview {
	/** Draw every 4 ticks (5 times per second). Dust lives longer than that, so the dots look steady. */
	private static final int EVERY_TICKS = 4;
	private static final DustParticleOptions SPOT = new DustParticleOptions(0xFFFFFF, 1.0f);
	private static final DustParticleOptions START = new DustParticleOptions(0xFFD84A, 1.2f);
	private static final DustParticleOptions ROW = new DustParticleOptions(0x55FF55, 0.9f);
	private static final DustParticleOptions WRONG = new DustParticleOptions(0xFF5555, 1.2f);

	private TrowelPreview() {
	}

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(TrowelPreview::tick);
	}

	private static void tick(Minecraft minecraft) {
		ClientLevel level = minecraft.level;
		if (minecraft.player == null || level == null || minecraft.isPaused() || level.getGameTime() % EVERY_TICKS != 0) {
			return;
		}
		ItemStack trowel = minecraft.player.getMainHandItem();
		if (!trowel.is(ModItems.TROWEL)) {
			trowel = minecraft.player.getOffhandItem();
			if (!trowel.is(ModItems.TROWEL)) {
				return;
			}
		}

		GlobalPos savedStart = trowel.get(ModDataComponents.ROW_START);
		BlockPos start = savedStart != null && savedStart.dimension().equals(level.dimension()) ? savedStart.pos() : null;
		if (start != null) {
			PreviewParticles.dot(level, START, start, 3);
		}
		if (!(minecraft.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
			return;
		}
		BlockPos spot = TrowelItem.spotFor(hit.getBlockPos(), level.getBlockState(hit.getBlockPos()), hit.getDirection());
		if (start == null) {
			PreviewParticles.dot(level, SPOT, spot, 2);
			return;
		}

		// The same checks as TrowelItem.useOn (2nd click).
		Direction direction = spot.equals(start) ? Direction.UP : TrowelItem.straightDirection(start, spot);
		int length = start.distManhattan(spot) + 1;
		if (direction == null || length > TrowelMode.ROW.maxBlocks()) {
			PreviewParticles.dot(level, WRONG, spot, 3);
			return;
		}
		// Like the real placing: the row ends at the first spot that is not free.
		for (int i = 0; i < length; i++) {
			BlockPos pos = start.relative(direction, i);
			if (!level.getBlockState(pos).canBeReplaced()) {
				break;
			}
			PreviewParticles.dot(level, ROW, pos, 1);
		}
	}
}
