package io.github.semihkayli.siteworks.pour;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.semihkayli.siteworks.pour.MoldPour.Cell;
import io.github.semihkayli.siteworks.pour.MoldPour.Pos;
import io.github.semihkayli.siteworks.pour.MoldPour.Result;
import io.github.semihkayli.siteworks.pour.MoldPour.Status;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the mold pour, on a fake world: flat solid ground under y = 0, open air above it.
 * Run them with: gradlew test
 */
class MoldPourTest {
	/** A fake world. Every cell is open air, except the ground (y < 0) and the cells we set. */
	private static final class FakeGrid implements MoldPour.Grid {
		private final Map<Pos, Cell> cells = new HashMap<>();
		private boolean ground = true;

		@Override
		public Cell get(int x, int y, int z) {
			Cell cell = cells.get(new Pos(x, y, z));
			if (cell != null) {
				return cell;
			}
			return ground && y < 0 ? Cell.SOLID : Cell.OPEN;
		}

		FakeGrid set(int x, int y, int z, Cell cell) {
			cells.put(new Pos(x, y, z), cell);
			return this;
		}

		/** A ring of walls around an inside area from (x0, z0) to (x1, z1), from y = 0 up to height - 1. */
		FakeGrid mold(int x0, int z0, int x1, int z1, int height) {
			for (int y = 0; y < height; y++) {
				for (int x = x0 - 1; x <= x1 + 1; x++) {
					for (int z = z0 - 1; z <= z1 + 1; z++) {
						boolean inside = x >= x0 && x <= x1 && z >= z0 && z <= z1;
						if (!inside) {
							set(x, y, z, Cell.SOLID);
						}
					}
				}
			}
			return this;
		}

		/** Places the planned cells, like the game does (wet concrete = SOLID). */
		void apply(Result result) {
			result.cells().forEach(pos -> cells.put(pos, Cell.SOLID));
		}
	}

	private static Result pour(FakeGrid grid, int x, int y, int z, int amount) {
		return MoldPour.plan(grid, new Pos(x, y, z), amount);
	}

	private static long countAtY(Result result, int y) {
		return result.cells().stream().filter(pos -> pos.y() == y).count();
	}

	@Test
	void closedMoldFillsBottomUp() {
		// 3x3 inside, 3 high = 27 cells. One bucket = 16: the full bottom layer (9) + 7 on the 2nd layer.
		FakeGrid grid = new FakeGrid().mold(0, 0, 2, 2, 3);
		Result result = pour(grid, 1, 0, 1, 16);

		assertEquals(Status.POURED, result.status());
		assertEquals(16, result.cells().size());
		assertEquals(9, countAtY(result, 0));
		assertEquals(7, countAtY(result, 1));
		// Bottom-up: the first 9 cells are all on the floor.
		assertTrue(result.cells().subList(0, 9).stream().allMatch(pos -> pos.y() == 0));
	}

	@Test
	void pourFromAboveDropsToTheFloor() {
		// Click high above the mold (like aiming into it from the top): the concrete falls to the floor first.
		FakeGrid grid = new FakeGrid().mold(0, 0, 2, 2, 3);
		Result result = pour(grid, 1, 5, 1, 16);

		assertEquals(9, countAtY(result, 0));
	}

	@Test
	void smallMoldIsFullAndKeepsTheRest() {
		// 2x2 inside, 2 high = 8 cells. The bucket keeps the other 8.
		FakeGrid grid = new FakeGrid().mold(0, 0, 1, 1, 2);
		Result result = pour(grid, 0, 0, 0, 16);

		assertEquals(Status.FULL, result.status());
		assertEquals(8, result.cells().size());
	}

	@Test
	void openMoldLeaksAndUsesNothing() {
		// A 3x3 mold with one wall block missing at the floor.
		FakeGrid grid = new FakeGrid().mold(0, 0, 2, 2, 3).set(3, 0, 1, Cell.OPEN);
		Result result = pour(grid, 1, 0, 1, 16);

		assertEquals(Status.LEAKS, result.status());
		assertEquals(0, result.cells().size());
	}

	@Test
	void noMoldAtAllLeaks() {
		// Pouring onto flat open ground.
		Result result = pour(new FakeGrid(), 0, 0, 0, 16);

		assertEquals(Status.LEAKS, result.status());
	}

	@Test
	void gapHigherUpStopsAtThatLevel() {
		// A wall block is missing on the 2nd layer: the 1st layer fills, then the concrete would spill.
		FakeGrid grid = new FakeGrid().mold(0, 0, 2, 2, 3).set(3, 1, 1, Cell.OPEN);
		Result result = pour(grid, 1, 0, 1, 16);

		assertEquals(Status.FULL, result.status());
		assertEquals(9, result.cells().size());
		assertEquals(9, countAtY(result, 0));
	}

	@Test
	void holeInTheGroundIsAMold() {
		// A 2x2 pit, 2 deep, dug into the ground. It fills (8), then the ground level spreads = full.
		FakeGrid grid = new FakeGrid();
		for (int x = 0; x <= 1; x++) {
			for (int z = 0; z <= 1; z++) {
				grid.set(x, -1, z, Cell.OPEN).set(x, -2, z, Cell.OPEN);
			}
		}
		Result result = pour(grid, 0, 0, 0, 16);

		assertEquals(Status.FULL, result.status());
		assertEquals(8, result.cells().size());
		assertTrue(result.cells().stream().allMatch(pos -> pos.y() < 0));
	}

	@Test
	void rebarCellsAreFilled() {
		FakeGrid grid = new FakeGrid().mold(0, 0, 1, 1, 1).set(0, 0, 0, Cell.REBAR);
		Result result = pour(grid, 1, 0, 1, 16);

		assertEquals(4, result.cells().size());
		assertTrue(result.cells().contains(new Pos(0, 0, 0)));
	}

	@Test
	void lShapedMoldFillsBothLegs() {
		// Inside cells: (0..3, z=0) and (0, z=1..3) = an L of 7 cells, 1 high. Everything else is wall.
		FakeGrid grid = new FakeGrid();
		for (int x = -1; x <= 4; x++) {
			for (int z = -1; z <= 4; z++) {
				boolean inside = (z == 0 && x >= 0 && x <= 3) || (x == 0 && z >= 1 && z <= 3);
				if (!inside) {
					grid.set(x, 0, z, Cell.SOLID);
				}
			}
		}
		Result result = pour(grid, 3, 0, 0, 16);

		assertEquals(Status.FULL, result.status());
		assertEquals(7, result.cells().size());
		assertTrue(result.cells().contains(new Pos(0, 0, 3)));
	}

	@Test
	void secondBucketFinishesTheLowerLayerFirst() {
		// Bucket 1 pours only 5 into a 3x3 floor. Bucket 2 is poured on TOP of that concrete,
		// but the concrete flows down and finishes the floor (4 more) before rising.
		FakeGrid grid = new FakeGrid().mold(0, 0, 2, 2, 3);
		Result first = pour(grid, 1, 0, 1, 5);
		grid.apply(first);
		Result second = pour(grid, 1, 1, 1, 16);

		assertEquals(Status.POURED, second.status());
		assertEquals(4, countAtY(second, 0));
		assertEquals(9, countAtY(second, 1));
		assertEquals(3, countAtY(second, 2));
	}

	@Test
	void every32WideMoldWorksBut33Leaks() {
		FakeGrid ok = new FakeGrid().mold(0, 0, 31, 0, 1);
		assertEquals(16, pour(ok, 0, 0, 0, 16).cells().size());

		FakeGrid tooWide = new FakeGrid().mold(0, 0, 32, 0, 1);
		assertEquals(Status.LEAKS, pour(tooWide, 0, 0, 0, 16).status());
	}

	@Test
	void noFloorLeaks() {
		FakeGrid grid = new FakeGrid();
		grid.ground = false;
		assertEquals(Status.LEAKS, pour(grid, 0, 0, 0, 16).status());
	}

	@Test
	void closedTopStopsThePour() {
		// 1 cell with a lid on top: only 1 block fits.
		FakeGrid grid = new FakeGrid().mold(0, 0, 0, 0, 1).set(0, 1, 0, Cell.SOLID);
		Result result = pour(grid, 0, 0, 0, 16);

		assertEquals(Status.FULL, result.status());
		assertEquals(List.of(new Pos(0, 0, 0)), result.cells());
	}

	@Test
	void everyCellIsSupported() {
		// Safe placing order: each cell has a solid block or an earlier cell under it.
		FakeGrid grid = new FakeGrid().mold(0, 0, 2, 2, 3);
		Result result = pour(grid, 1, 2, 1, 16);
		for (int i = 0; i < result.cells().size(); i++) {
			Pos pos = result.cells().get(i);
			Pos below = new Pos(pos.x(), pos.y() - 1, pos.z());
			boolean supported = grid.get(below.x(), below.y(), below.z()) == Cell.SOLID
					|| result.cells().subList(0, i).contains(below);
			assertTrue(supported, "not supported: " + pos);
		}
	}
}
