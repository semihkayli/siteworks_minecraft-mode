package io.github.semihkayli.siteworks.foundation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Foundation rule (Phase 3¾), the pure logic: it looks at ONE column of blocks (one x/z spot, straight up and down).
 *
 * A foundation = a stack of Reinforced Concrete layers that stands on a solid block (any block: stone, dirt, sand...).
 * Every layer allows 8 blocks of building height above the foundation's top: 1 layer = up to 8 high,
 * 2 layers = 9-16, 3 layers = 17-24... The blocks in that height are explosion-proof, and players can't build higher.
 *
 * No Minecraft classes on purpose: the unit tests run this on a fake column, without starting the game.
 */
public final class Foundation {
	/** Every foundation layer allows this many blocks of building height. */
	public static final int BLOCKS_PER_LAYER = 8;

	/** What a block is for the foundation rule. */
	public enum Cell {
		/** Reinforced Concrete: a foundation layer. */
		FOUNDATION,
		/** A solid block (stone, dirt, sand, bricks...): a foundation can stand on it. */
		SOLID,
		/** Anything else: air, water, plants, torches... A foundation can't stand on it. */
		OTHER
	}

	/** One column of the world, by height. The game version reads real blocks, the tests use a fake column. */
	public interface Column {
		Cell get(int y);
	}

	/** The whole world (many columns), for checking many blocks at once (an explosion). */
	public interface World {
		Cell get(int x, int y, int z);
	}

	public record Pos(int x, int y, int z) {
	}

	/**
	 * A stack of Reinforced Concrete layers in one column, from {@code bottom} to {@code top} (both included).
	 * {@code standing} = there is a solid block under the bottom layer.
	 */
	public record Stack(int bottom, int top, boolean standing) {
		public int layers() {
			return top - bottom + 1;
		}

		/** Only a stack that stands on a solid block is a real foundation (not one floating in the air). */
		public boolean isFoundation() {
			return standing;
		}

		/** How high the building on it may be (0 = not a foundation). */
		public int maxHeight() {
			return isFoundation() ? layers() * BLOCKS_PER_LAYER : 0;
		}

		/** The highest y a building block may have. */
		public int limitY() {
			return top + maxHeight();
		}

		/** Is a block at {@code y} part of the building this foundation carries (above it, up to the limit)? */
		public boolean carries(int y) {
			return isFoundation() && y > top && y <= limitY();
		}
	}

	private Foundation() {
	}

	/** How many foundation layers a building of {@code height} blocks needs: 1-8 = 1, 9-16 = 2, ... */
	public static int layersFor(int height) {
		return Math.max(1, (height + BLOCKS_PER_LAYER - 1) / BLOCKS_PER_LAYER);
	}

	/**
	 * The whole stack around {@code y} ({@code y} must be a FOUNDATION cell): up to its top, down to its bottom,
	 * and whether it stands on something. {@code minY}/{@code maxY} = the world's height limits.
	 */
	public static Stack stackAt(Column column, int y, int minY, int maxY) {
		int top = y;
		while (top + 1 <= maxY && column.get(top + 1) == Cell.FOUNDATION) {
			top++;
		}
		return stackDown(column, top, minY);
	}

	/**
	 * The foundation that carries a block at {@code y}: the first REAL foundation under it (looking straight down
	 * through rooms and walls). A stack that doesn't stand on a solid block (e.g. a reinforced floor over a room)
	 * is skipped, and we look further down. Null = no foundation under this block.
	 */
	public static Stack below(Column column, int y, int minY) {
		for (int cy = y - 1; cy >= minY; cy--) {
			if (column.get(cy) == Cell.FOUNDATION) {
				Stack stack = stackDown(column, cy, minY);
				if (stack.isFoundation()) {
					return stack;
				}
				cy = stack.bottom(); // skip this stack (the loop goes one lower)
			}
		}
		return null;
	}

	/**
	 * The hard stop (Phase 3¾ step 4, user's rule A: "stop only when the building grows taller").
	 * May a player put a block at {@code y}? It's refused only when it goes ON TOP of a protected block
	 * that is already at its foundation's limit. {@code blockBelow} = there is a block (not air) at {@code y - 1}.
	 * Returns the foundation whose limit it would break, or null = OK.
	 * So building beside a house, on a thicker part, or far above a deep bunker is never stopped.
	 */
	public static Stack tooTall(Column column, int y, boolean blockBelow, int minY) {
		Stack own = below(column, y, minY);
		if (own != null && own.carries(y)) {
			return null; // inside the allowed height of a foundation
		}
		if (!blockBelow) {
			return null; // it doesn't sit on the building
		}
		Stack under = below(column, y - 1, minY);
		return under != null && under.carries(y - 1) && y > under.limitY() ? under : null;
	}

	/**
	 * Does the block at {@code y} hold up a foundation (a solid block right under its bottom layer)?
	 * It's protected too (user, 2026-10-24): otherwise a chain of TNT digs away the ground under the foundation's edge,
	 * that part stops being a foundation, and the next explosions break the building above it.
	 */
	public static boolean holdsUp(Column column, int y) {
		return column.get(y) == Cell.SOLID && column.get(y + 1) == Cell.FOUNDATION;
	}

	/**
	 * Which of these blocks are protected: carried by a foundation, or holding one up? Used for explosions,
	 * which hit hundreds of blocks. Fast: in each column we start with the highest block and look down once.
	 * A lower block in the same column has the same foundation, unless it is at or below that foundation's top
	 * (then we look again from there).
	 */
	public static Set<Pos> protectedBlocks(World world, Collection<Pos> positions, int minY) {
		List<Pos> highestFirst = new ArrayList<>(positions);
		highestFirst.sort(Comparator.comparingInt(Pos::y).reversed());
		Map<Long, Stack> found = new HashMap<>(); // column -> its foundation (null value = no foundation under it)
		Set<Pos> safe = new HashSet<>();
		for (Pos pos : highestFirst) {
			long key = ((long) pos.x() << 32) | (pos.z() & 0xFFFFFFFFL);
			Column column = y -> world.get(pos.x(), y, pos.z());
			Stack stack;
			if (!found.containsKey(key)) {
				stack = below(column, pos.y(), minY);
				found.put(key, stack);
			} else {
				stack = found.get(key);
				if (stack != null && pos.y() <= stack.top()) {
					stack = below(column, pos.y(), minY); // this block is inside or under that foundation
					found.put(key, stack);
				}
			}
			if ((stack != null && stack.carries(pos.y())) || holdsUp(column, pos.y())) {
				safe.add(pos);
			}
		}
		return safe;
	}

	/** The stack whose top is {@code top}: go down while it's Reinforced Concrete, then look at the block under it. */
	private static Stack stackDown(Column column, int top, int minY) {
		int bottom = top;
		while (bottom - 1 >= minY && column.get(bottom - 1) == Cell.FOUNDATION) {
			bottom--;
		}
		boolean standing = bottom - 1 >= minY && column.get(bottom - 1) == Cell.SOLID;
		return new Stack(bottom, top, standing);
	}
}
