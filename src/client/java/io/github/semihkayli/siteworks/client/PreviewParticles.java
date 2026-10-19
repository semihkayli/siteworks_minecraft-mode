package io.github.semihkayli.siteworks.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;

/** The colored dust dots of our previews (Trowel, Concrete Bucket). Client only: only you see them. */
final class PreviewParticles {
	private PreviewParticles() {
	}

	/** {@code count} dust particles near the middle of a block spot. */
	static void dot(ClientLevel level, DustParticleOptions color, BlockPos pos, int count) {
		for (int i = 0; i < count; i++) {
			level.addParticle(color,
					pos.getX() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 0.3,
					pos.getY() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 0.3,
					pos.getZ() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 0.3,
					0.0, 0.0, 0.0);
		}
	}
}
