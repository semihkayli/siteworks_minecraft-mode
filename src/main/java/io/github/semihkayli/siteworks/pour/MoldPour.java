package io.github.semihkayli.siteworks.pour;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
 * so the mold is full. A protected cell (spawn protection, world border, outside the world) stops the pour.
 *
 * The result also tells WHERE the problem is (the hole in the formwork), so the preview can show a red dot there.
 *
 * No Minecraft classes on purpose: the unit tests run this on a fake grid, without starting the game.
 */
public final class MoldPour {
	/** One mold can be at most 32 blocks wide in x and in z (not a limit on the building, see docs). */
	public static final int MAX_LAYER_SIZE = 32;
	/** Concrete falls at most this far down. Deeper = no floor = leaks. */
	public static final int MAX_DROP = 32;
	/**
	 * Aim help: if the pour leaks, we also try the open spots up to this far to the side that have room below.
	 * So clicking the top of a mold wall still pours into the mold.
	 */
	public static final int AIM_HELP = 2;

	/** What a cell is for the pour. */
	public enum Cell {
		/** Air, water, plants: concrete can go here. */
		OPEN,
		/** Rebar: concrete can go here, and it cures into Reinforced Concrete. */
		REBAR,
		/** Wet or cured concrete: a wall or floor. Landing on it with nothing to fill = the mold is already full. */
		CONCRETE,
		/** Anything else: a mold wall or floor. */
		SOLID,
		/** An open cell where concrete may NOT go: spawn protection, the world border, outside the world. */
		BLOCKED
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
		LEAKS,
		/** It ran into a protected cell before anything was poured. Nothing was poured. */
		PROTECTED
	}

	/**
	 * {@code cells} are in a safe placing order: every cell is on a solid block or an earlier cell.
	 * {@code problem} = where it leaks (the hole), the protected cell it ran into, or null.
	 */
	public record Result(Status status, List<Pos> cells, Pos problem) {
	}

	private static final int[][] SIDES = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

	private final Grid grid;
	private final Set<Pos> planned = new LinkedHashSet<>();
	private Pos problem;
	private boolean blocked;

	private MoldPour(Grid grid) {
		this.grid = grid;
	}

	/** Plans a pour of up to {@code amount} blocks, starting at {@code start}. */
	public static Result plan(Grid grid, Pos start, int amount) {
		Result result = new MoldPour(grid).run(start, amount);
		if (result.status() != Status.LEAKS) {
			return result;
		}
		// Aim help: maybe you clicked a bit too high (e.g. the top of a mold wall). Try the spots right next to it.
		for (Pos near : spotsNear(grid, start)) {
			Result retry = new MoldPour(grid).run(near, amount);
			if (retry.status() == Status.POURED || retry.status() == Status.FULL) {
				return retry;
			}
		}
		// Nothing to fill, and the concrete would land on concrete: the mold under it is already full.
		if (landsOnConcrete(grid, start)) {
			return new Result(Status.FULL, List.of(), result.problem());
		}
		return result;
	}

	private Result run(Pos start, int amount) {
		if (cell(start) == Cell.BLOCKED) {
			markBlocked(start);
			return stop();
		}
		if (!isOpen(start)) {
			problem = start;
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
				return stop(); // too wide (leaks / spills) or a protected cell
			}
			if (layer.isEmpty()) {
				return stop(); // closed top
			}

			// Water flows down first: is there an open cell under this layer?
			Pos hole = findHole(layer);
			if (hole != null) {
				Pos below = hole.below();
				if (cell(below) == Cell.BLOCKED) {
					markBlocked(below);
					return stop();
				}
				Pos lower = dropFrom(below);
				if (lower == null) {
					return stop();
				}
				seeds = List.of(lower);
				continue;
			}

			for (Pos pos : layer) {
				planned.add(pos);
				if (planned.size() == amount) {
					return new Result(Status.POURED, List.copyOf(planned), null);
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
		if (planned.isEmpty()) {
			return new Result(blocked ? Status.PROTECTED : Status.LEAKS, List.of(), problem);
		}
		return new Result(Status.FULL, List.copyOf(planned), problem);
	}

	private void markBlocked(Pos pos) {
		blocked = true;
		problem = pos;
	}

	/** Falls straight down from an open cell to the lowest open cell. Null = no floor within MAX_DROP, or protected. */
	private Pos dropFrom(Pos pos) {
		Pos from = pos;
		for (int i = 0; i <= MAX_DROP; i++) {
			Pos below = pos.below();
			if (isFloor(below)) {
				return pos;
			}
			if (cell(below) == Cell.BLOCKED) {
				markBlocked(below);
				return null;
			}
			pos = below;
		}
		problem = from; // the red dot goes where it starts to fall, not 32 blocks down
		return null;
	}

	/**
	 * All open cells on one y level that connect to the seeds (sideways only), nearest first.
	 * Null = wider than MAX_LAYER_SIZE in x or z (then {@code problem} = the hole), or it touches a protected cell.
	 */
	private List<Pos> layerFrom(List<Pos> seeds) {
		List<Pos> layer = new ArrayList<>();
		// Where each cell was reached from (a seed points to itself). Used to find the hole.
		Map<Pos, Pos> cameFrom = new HashMap<>();
		Queue<Pos> queue = new ArrayDeque<>();
		for (Pos seed : seeds) {
			if (isOpen(seed) && !cameFrom.containsKey(seed)) {
				cameFrom.put(seed, seed);
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
				problem = holeOnPath(cameFrom, pos);
				return null;
			}
			for (int[] side : SIDES) {
				Pos next = pos.offset(side[0], side[1]);
				if (cell(next) == Cell.BLOCKED) {
					markBlocked(next);
					return null;
				}
				if (isOpen(next) && !cameFrom.containsKey(next)) {
					cameFrom.put(next, pos);
					queue.add(next);
				}
			}
		}
		return layer;
	}

	/**
	 * Where does the concrete get out of the mold? We walk back from the far cell to the start.
	 * The hole is the last narrow spot on that way (walls on both sides, like a missing formwork block).
	 * No narrow spot: the last cell next to any wall (an open side of the mold). No wall at all: the start.
	 */
	private Pos holeOnPath(Map<Pos, Pos> cameFrom, Pos far) {
		Pos nextToWall = null;
		Pos pos = far;
		while (true) {
			Pos prev = cameFrom.get(pos);
			if (prev.equals(pos)) {
				return nextToWall != null ? nextToWall : pos;
			}
			boolean alongX = pos.x() != prev.x();
			boolean wallA = !isOpen(alongX ? pos.offset(0, 1) : pos.offset(1, 0));
			boolean wallB = !isOpen(alongX ? pos.offset(0, -1) : pos.offset(-1, 0));
			if (wallA && wallB) {
				return pos;
			}
			if (nextToWall == null && (wallA || wallB)) {
				nextToWall = pos;
			}
			pos = prev;
		}
	}

	private Pos findHole(List<Pos> layer) {
		for (Pos pos : layer) {
			if (!isFloor(pos.below())) {
				return pos;
			}
		}
		return null;
	}

	private Cell cell(Pos pos) {
		return grid.get(pos.x(), pos.y(), pos.z());
	}

	/** Open = concrete can go here: air, plants, rebar... and not already planned in this pour. */
	private boolean isOpen(Pos pos) {
		return isFillable(cell(pos)) && !planned.contains(pos);
	}

	/** Floor = concrete can stand on it: a solid block, concrete, or concrete planned earlier in this pour. */
	private boolean isFloor(Pos pos) {
		Cell cell = cell(pos);
		return cell == Cell.SOLID || cell == Cell.CONCRETE || planned.contains(pos);
	}

	private static boolean isFillable(Cell cell) {
		return cell == Cell.OPEN || cell == Cell.REBAR;
	}

	/** Aim help: open spots up to AIM_HELP to the side of the start (same height) that have room below them. Nearest first. */
	private static List<Pos> spotsNear(Grid grid, Pos start) {
		List<Pos> spots = new ArrayList<>();
		Set<Pos> seen = new HashSet<>(List.of(start));
		List<Pos> ring = List.of(start);
		for (int step = 0; step < AIM_HELP; step++) {
			List<Pos> nextRing = new ArrayList<>();
			for (Pos pos : ring) {
				for (int[] side : SIDES) {
					Pos next = pos.offset(side[0], side[1]);
					if (isFillable(grid.get(next.x(), next.y(), next.z())) && seen.add(next)) {
						nextRing.add(next);
						Pos below = next.below();
						if (isFillable(grid.get(below.x(), below.y(), below.z()))) {
							spots.add(next);
						}
					}
				}
			}
			ring = nextRing;
		}
		return spots;
	}

	/** Would concrete poured at {@code start} fall onto concrete (wet or cured)? */
	private static boolean landsOnConcrete(Grid grid, Pos start) {
		Pos pos = start;
		for (int i = 0; i <= MAX_DROP; i++) {
			Pos below = pos.below();
			Cell cell = grid.get(below.x(), below.y(), below.z());
			if (cell == Cell.CONCRETE) {
				return true;
			}
			if (!isFillable(cell)) {
				return false;
			}
			pos = below;
		}
		return false;
	}
}
