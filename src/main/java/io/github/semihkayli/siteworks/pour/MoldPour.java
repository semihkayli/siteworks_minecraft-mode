package io.github.semihkayli.siteworks.pour;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * The mold pour: decides WHERE the concrete from one bucket goes. It doesn't change the world.
 *
 * Concrete behaves like water:
 * <ol>
 *   <li>It drops to the lowest point under the pour point.</li>
 *   <li>It fills that layer (flood fill / taşma doldurma, nearest cells first), then rises layer by layer.</li>
 *   <li>If a layer has an open cell under it, the concrete flows down there first.</li>
 * </ol>
 * It stops when the bucket is empty, when the top is closed, or when a layer is too wide to be a mold
 * (more than {@link #MAX_LAYER_SIZE} in x or z). Too wide on the FIRST layer = the formwork leaks,
 * and nothing is used. Too wide on a higher layer = the concrete would spill over the lowest wall,
 * so the mold is full.
 *
 * No Minecraft classes on purpose: the unit tests run this on a fake grid, without starting the game.
 */
public final class MoldPour {
	/** One mold can be at most 32 blocks wide in x and in z (not a limit on the building, see docs). */
	public static final int MAX_LAYER_SIZE = 32;
	/** Concrete falls at most this far down. Deeper = no floor = leaks. */
	public static final int MAX_DROP = 32;

	/** What a cell is for the pour. */
	public enum Cell {
		/** Air, water, plants: concrete can go here. */
		OPEN,
		/** Rebar: concrete can go here, and it cures into Reinforced Concrete. */
		REBAR,
		/** Anything else: a mold wall or floor. Wet concrete is SOLID too. */
		SOLID
	}

	/** The world as the pour sees it. The game version reads real blocks, the tests use a fake grid. */
	public interface Grid {
		Cell get(int x, int y, int z);
	}

	public record Pos(int x, int y, int z) {
		Pos below() {
			return new Pos(x, y - 1, z);
		}

		Pos above() {
			return new Pos(x, y + 1, z);
		}

		Pos offset(int dx, int dz) {
			return new Pos(x + dx, y, z + dz);
		}
	}

	public enum Status {
		/** The whole amount was poured. */
		POURED,
		/** The mold took less than the amount (maybe 0): it is full, or the next layer would spill. */
		FULL,
		/** The first layer is open: nothing was poured. "The formwork leaks!" */
		LEAKS
	}

	/** {@code cells} are in a safe placing order: every cell is on a solid block or an earlier cell. */
	public record Result(Status status, List<Pos> cells) {
	}

	private static final int[][] SIDES = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

	private final Grid grid;
	private final Set<Pos> planned = new LinkedHashSet<>();

	private MoldPour(Grid grid) {
		this.grid = grid;
	}

	/** Plans a pour of up to {@code amount} blocks, starting at {@code start}. */
	public static Result plan(Grid grid, Pos start, int amount) {
		return new MoldPour(grid).run(start, amount);
	}

	private Result run(Pos start, int amount) {
		if (!isOpen(start)) {
			return stop();
		}
		Pos bottom = dropFrom(start);
		if (bottom == null) {
			return stop();
		}
		List<Pos> seeds = List.of(bottom);

		// Each round either fills cells, moves down into a lower pocket, or ends. The limit is only a safety net.
		for (int round = 0; round < 1000; round++) {
			List<Pos> layer = layerFrom(seeds);
			if (layer == null) {
				return stop(); // too wide: leaks (first layer) or spills (higher layer)
			}
			if (layer.isEmpty()) {
				return stop(); // closed top
			}

			// Water flows down first: is there an open cell under this layer?
			Pos hole = findHole(layer);
			if (hole != null) {
				Pos lower = dropFrom(hole.below());
				if (lower == null) {
					return stop();
				}
				seeds = List.of(lower);
				continue;
			}

			for (Pos pos : layer) {
				planned.add(pos);
				if (planned.size() == amount) {
					return new Result(Status.POURED, List.copyOf(planned));
				}
			}

			// The layer is full: rise to the cells above it.
			List<Pos> next = new ArrayList<>();
			for (Pos pos : layer) {
				if (isOpen(pos.above())) {
					next.add(pos.above());
				}
			}
			seeds = next;
		}
		return stop();
	}

	/** Ends the pour with what is planned so far. */
	private Result stop() {
		return planned.isEmpty()
				? new Result(Status.LEAKS, List.of())
				: new Result(Status.FULL, List.copyOf(planned));
	}

	/** Falls straight down from an open cell to the lowest open cell. Null = no floor within MAX_DROP. */
	private Pos dropFrom(Pos pos) {
		for (int i = 0; i <= MAX_DROP; i++) {
			if (!isOpen(pos.below())) {
				return pos;
			}
			pos = pos.below();
		}
		return null;
	}

	/**
	 * All open cells on one y level that connect to the seeds (sideways only), nearest first.
	 * Null = wider than MAX_LAYER_SIZE in x or z.
	 */
	private List<Pos> layerFrom(List<Pos> seeds) {
		List<Pos> layer = new ArrayList<>();
		Set<Pos> seen = new HashSet<>();
		Queue<Pos> queue = new ArrayDeque<>();
		for (Pos seed : seeds) {
			if (isOpen(seed) && seen.add(seed)) {
				queue.add(seed);
			}
		}
		int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;
		while (!queue.isEmpty()) {
			Pos pos = queue.poll();
			layer.add(pos);
			minX = Math.min(minX, pos.x());
			maxX = Math.max(maxX, pos.x());
			minZ = Math.min(minZ, pos.z());
			maxZ = Math.max(maxZ, pos.z());
			if (maxX - minX >= MAX_LAYER_SIZE || maxZ - minZ >= MAX_LAYER_SIZE) {
				return null;
			}
			for (int[] side : SIDES) {
				Pos next = pos.offset(side[0], side[1]);
				if (isOpen(next) && seen.add(next)) {
					queue.add(next);
				}
			}
		}
		return layer;
	}

	private Pos findHole(List<Pos> layer) {
		for (Pos pos : layer) {
			if (isOpen(pos.below())) {
				return pos;
			}
		}
		return null;
	}

	/** Open = concrete can go here: not solid, and not already planned in this pour. */
	private boolean isOpen(Pos pos) {
		return grid.get(pos.x(), pos.y(), pos.z()) != Cell.SOLID && !planned.contains(pos);
	}
}
