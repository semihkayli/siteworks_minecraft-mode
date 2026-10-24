package io.github.semihkayli.siteworks.foundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.semihkayli.siteworks.foundation.Foundation.Cell;
import io.github.semihkayli.siteworks.foundation.Foundation.Stack;
import java.util.HashMap;
import java.util.Map;
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

	@Test
	void foundationAtTheBottomOfTheWorldIsNoFoundation() {
		// Nothing under the lowest layer (the world ends): not on firm ground.
		FakeColumn column = new FakeColumn().set(MIN_Y, MIN_Y + 1, Cell.FOUNDATION);

		assertFalse(Foundation.stackAt(column, MIN_Y, MIN_Y, MAX_Y).isFoundation());
	}
}
