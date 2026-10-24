package io.github.semihkayli.siteworks.foundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.semihkayli.siteworks.foundation.Foundation.Cell;
import io.github.semihkayli.siteworks.foundation.Foundation.Stack;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Foundation rule, on a fake column: every height is OTHER (air), except the cells we set.
 * Run them with: gradlew test
 */
class FoundationTest {
	private static final int MIN_Y = -64;
	private static final int MAX_Y = 319;

	/** A fake column of blocks. */
	private static final class FakeColumn implements Foundation.Column {
		private final Map<Integer, Cell> cells = new HashMap<>();

		@Override
		public Cell get(int y) {
			return cells.getOrDefault(y, Cell.OTHER);
		}

		/** Sets the cells from {@code fromY} to {@code toY} (both included). */
		FakeColumn set(int fromY, int toY, Cell cell) {
			for (int y = fromY; y <= toY; y++) {
				cells.put(y, cell);
			}
			return this;
		}
	}

	@Test
	void oneLayerOnStoneCarriesEightBlocks() {
		// Stone at y = 0, one layer of Reinforced Concrete at y = 1: the building may go up to y = 9.
		FakeColumn column = new FakeColumn().set(0, 0, Cell.SOLID).set(1, 1, Cell.FOUNDATION);
		Stack stack = Foundation.stackAt(column, 1, MIN_Y, MAX_Y);

		assertTrue(stack.isFoundation());
		assertEquals(1, stack.layers());
		assertEquals(8, stack.maxHeight());
		assertEquals(9, stack.limitY());
		assertTrue(stack.carries(2));
		assertTrue(stack.carries(9));
		assertFalse(stack.carries(10));
		assertFalse(stack.carries(1)); // the foundation itself isn't "the building"
	}

	@Test
	void threeLayersCarryTwentyFourBlocks() {
		// Clicking the middle layer finds the whole stack.
		FakeColumn column = new FakeColumn().set(0, 0, Cell.SOLID).set(1, 3, Cell.FOUNDATION);
		Stack stack = Foundation.stackAt(column, 2, MIN_Y, MAX_Y);

		assertEquals(new Stack(1, 3, true), stack);
		assertEquals(24, stack.maxHeight());
		assertEquals(27, stack.limitY());
	}

	@Test
	void layersNeededForAHeight() {
		// The user's examples: 5 high = 1 layer, 12 high = 2 layers.
		assertEquals(1, Foundation.layersFor(1));
		assertEquals(1, Foundation.layersFor(5));
		assertEquals(1, Foundation.layersFor(8));
		assertEquals(2, Foundation.layersFor(9));
		assertEquals(2, Foundation.layersFor(12));
		assertEquals(3, Foundation.layersFor(17));
	}

	@Test
	void withWaterOrAirUnderItIsNoFoundation() {
		// Under the stack is OTHER (water, air, a plant...): it doesn't stand on anything, so it carries nothing.
		FakeColumn column = new FakeColumn().set(0, 0, Cell.OTHER).set(1, 2, Cell.FOUNDATION);
		Stack stack = Foundation.stackAt(column, 1, MIN_Y, MAX_Y);

		assertFalse(stack.standing());
		assertFalse(stack.isFoundation());
		assertEquals(0, stack.maxHeight());
		assertFalse(stack.carries(3));
		assertNull(Foundation.below(column, 5, MIN_Y));
	}

	@Test
	void floatingInTheAirItIsNoFoundation() {
		FakeColumn column = new FakeColumn().set(10, 10, Cell.FOUNDATION);
		Stack stack = Foundation.stackAt(column, 10, MIN_Y, MAX_Y);

		assertFalse(stack.standing());
		assertFalse(stack.isFoundation());
	}

	@Test
	void belowLooksThroughRoomsAndWalls() {
		// Stone, 2 foundation layers (y 1-2), then a room (air) and a wall block above it.
		FakeColumn column = new FakeColumn().set(0, 0, Cell.SOLID).set(1, 2, Cell.FOUNDATION).set(6, 6, Cell.SOLID);
		Stack stack = Foundation.below(column, 8, MIN_Y);

		assertEquals(new Stack(1, 2, true), stack);
		assertTrue(stack.carries(8));
	}

	@Test
	void belowSkipsAReinforcedFloorOverARoom() {
		// The real foundation at y = 1, a reinforced floor at y = 5 over a room (air under it): the floor is skipped.
		FakeColumn column = new FakeColumn().set(0, 0, Cell.SOLID).set(1, 1, Cell.FOUNDATION).set(5, 5, Cell.FOUNDATION);
		Stack stack = Foundation.below(column, 7, MIN_Y);

		assertEquals(new Stack(1, 1, true), stack);
	}

	@Test
	void noFoundationUnderABlock() {
		FakeColumn column = new FakeColumn().set(MIN_Y, 0, Cell.SOLID);

		assertNull(Foundation.below(column, 5, MIN_Y));
	}

	/** A fake world: OTHER (air) everywhere, except the cells we set. */
	private static final class FakeWorld implements Foundation.World {
		private final Map<Foundation.Pos, Cell> cells = new HashMap<>();

		@Override
		public Cell get(int x, int y, int z) {
			return cells.getOrDefault(new Foundation.Pos(x, y, z), Cell.OTHER);
		}

		FakeWorld set(int x, int y, int z, Cell cell) {
			cells.put(new Foundation.Pos(x, y, z), cell);
			return this;
		}
	}

	private static Foundation.Pos pos(int x, int y, int z) {
		return new Foundation.Pos(x, y, z);
	}

	@Test
	void explosionKeepsOnlyTheBuildingAboveTheFoundation() {
		// A 1-layer foundation at y = 1 (x 0..2, z = 0) on solid ground at y = 0: it carries y 2..9.
		FakeWorld world = new FakeWorld();
		for (int x = 0; x <= 2; x++) {
			world.set(x, 0, 0, Cell.SOLID).set(x, 1, 0, Cell.FOUNDATION);
		}
		Set<Foundation.Pos> carried = Foundation.carried(world,
				List.of(pos(1, 5, 0), pos(1, 9, 0), pos(1, 10, 0), pos(5, 5, 0), pos(1, 1, 0), pos(1, 0, 0)), MIN_Y);

		assertEquals(Set.of(pos(1, 5, 0), pos(1, 9, 0)), carried);
	}

	@Test
	void explosionWithManyHeightsInOneColumn() {
		// Column x = z = 0: foundation at y = 1 (on solid y = 0), walls y 2..5, a 2nd foundation at y = 6 on the walls.
		// The 2nd one carries y 7..14, the 1st one carries y 2..9 (so also the 2nd foundation block itself).
		FakeWorld world = new FakeWorld().set(0, 0, 0, Cell.SOLID).set(0, 1, 0, Cell.FOUNDATION).set(0, 6, 0, Cell.FOUNDATION);
		for (int y = 2; y <= 5; y++) {
			world.set(0, y, 0, Cell.SOLID);
		}
		Set<Foundation.Pos> carried = Foundation.carried(world,
				List.of(pos(0, 3, 0), pos(0, 14, 0), pos(0, 15, 0), pos(0, 6, 0), pos(0, 1, 0)), MIN_Y);

		assertEquals(Set.of(pos(0, 3, 0), pos(0, 14, 0), pos(0, 6, 0)), carried);
	}

	@Test
	void foundationAtTheBottomOfTheWorldIsNoFoundation() {
		// Nothing under the lowest layer (the world ends): not on firm ground.
		FakeColumn column = new FakeColumn().set(MIN_Y, MIN_Y + 1, Cell.FOUNDATION);

		assertFalse(Foundation.stackAt(column, MIN_Y, MIN_Y, MAX_Y).isFoundation());
	}
}
