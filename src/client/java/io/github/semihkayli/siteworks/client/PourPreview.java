package io.github.semihkayli.siteworks.client;

import io.github.semihkayli.siteworks.item.ConcreteBucketItem;
import io.github.semihkayli.siteworks.pour.MoldPour;
import io.github.semihkayli.siteworks.registry.ModItems;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * The Concrete Bucket's pour preview (Phase 3½ step R3), client only. While you hold the bucket and look at a block,
 * it runs the same pour plan as the real pour (MoldPour, on your copy of the world) and shows it with dust:
 * - GRAY dots: where the concrete will go.
 * - A RED dot: where the mold leaks (the hole), or the protected spot that stops the pour.
 * - Nothing: the mold is full (or there's nothing to fill).
 */
public final class PourPreview {
	/** Draw every 4 ticks (5 times per second), like the trowel preview. */
	private static final int EVERY_TICKS = 4;
	private static final DustParticleOptions CONCRETE = new DustParticleOptions(0x9A9A9A, 1.0f);
	private static final DustParticleOptions PROBLEM = new DustParticleOptions(0xFF5555, 1.2f);

	private PourPreview() {
	}

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(PourPreview::tick);
	}

	private static void tick(Minecraft minecraft) {
		ClientLevel level = minecraft.level;
		if (minecraft.player == null || level == null || minecraft.isPaused() || level.getGameTime() % EVERY_TICKS != 0) {
			return;
		}
		ItemStack bucket = minecraft.player.getMainHandItem();
		if (!bucket.is(ModItems.CONCRETE_BUCKET)) {
			bucket = minecraft.player.getOffhandItem();
			if (!bucket.is(ModItems.CONCRETE_BUCKET)) {
				return;
			}
		}
		if (!(minecraft.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
			return;
		}

		// The same start and plan as ConcreteBucketItem.useOn, so the preview shows exactly what a click does.
		BlockPos start = ConcreteBucketItem.startFor(level, hit.getBlockPos(), hit.getDirection());
		MoldPour.Result result = ConcreteBucketItem.plan(level, minecraft.player, start, ConcreteBucketItem.getAmount(bucket));
		for (MoldPour.Pos cell : result.cells()) {
			PreviewParticles.dot(level, CONCRETE, toBlockPos(cell), 1);
		}
		boolean failed = result.status() == MoldPour.Status.LEAKS || result.status() == MoldPour.Status.PROTECTED;
		if (failed && result.problem() != null) {
			PreviewParticles.dot(level, PROBLEM, toBlockPos(result.problem()), 3);
		}
	}

	private static BlockPos toBlockPos(MoldPour.Pos pos) {
		return new BlockPos(pos.x(), pos.y(), pos.z());
	}
}
