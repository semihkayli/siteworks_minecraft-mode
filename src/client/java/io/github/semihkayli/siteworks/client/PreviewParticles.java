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

	/**
	 * {@code count} dust particles ON the face of block {@code wall} that looks at the open spot {@code from}.
	 * Used to point at one block (e.g. the stone that should be formwork) without hiding the dots inside it.
	 */
	static void dotOnFace(ClientLevel level, DustParticleOptions color, BlockPos from, BlockPos wall, int count) {
		// From the middle of the open spot, 0.45 of the way to the wall = just in front of the wall's face.
		double x = from.getX() + 0.5 + (wall.getX() - from.getX()) * 0.45;
		double y = from.getY() + 0.5 + (wall.getY() - from.getY()) * 0.45;
		double z = from.getZ() + 0.5 + (wall.getZ() - from.getZ()) * 0.45;
		for (int i = 0; i < count; i++) {
			level.addParticle(color,
					x + (level.getRandom().nextDouble() - 0.5) * 0.4,
					y + (level.getRandom().nextDouble() - 0.5) * 0.4,
					z + (level.getRandom().nextDouble() - 0.5) * 0.4,
					0.0, 0.0, 0.0);
		}
	}
}
