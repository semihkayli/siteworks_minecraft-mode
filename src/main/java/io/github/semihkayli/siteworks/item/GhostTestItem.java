package io.github.semihkayli.siteworks.item;

import com.mojang.math.Transformation;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Brightness;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * TEST ITEM (Phase 1 "risk first" ghost test). Creative only, no recipe.
 * Spawns small glowing Block Display entities, to see how the Phase 5 hologram will look and perform.
 * <ul>
 *   <li>Right-click a block: a small 3x3x3 demo house (blue = "place here", 2 red = "wrong block").</li>
 *   <li>Sneak + right-click a block: an 8x8x8 cube = 512 ghosts (the planned max), for a lag test.</li>
 *   <li>Right-click the air: remove all test ghosts within 128 blocks.</li>
 * </ul>
 */
public class GhostTestItem extends Item {
	/** Every test ghost gets this entity tag, so we can find and remove them later. */
	public static final String GHOST_TAG = "siteworks_ghost_test";
	private static final float SCALE = 0.8f;
	private static final int BLUE = 0x55CCFF;
	private static final int RED = 0xFF3333;

	public GhostTestItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		// Entities are spawned on the server only. The server tells every client about them.
		if (!(context.getLevel() instanceof ServerLevel level)) {
			return InteractionResult.SUCCESS;
		}
		Player player = context.getPlayer();
		BlockPos start = context.getClickedPos().relative(context.getClickedFace());
		int count = player != null && player.isShiftKeyDown() ? spawnBigCube(level, start) : spawnDemoHouse(level, start);
		if (player != null) {
			player.sendOverlayMessage(Component.translatable("message.siteworks.ghost_test.spawned", count));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level instanceof ServerLevel serverLevel) {
			AABB area = player.getBoundingBox().inflate(128);
			List<Display.BlockDisplay> ghosts = serverLevel.getEntities(EntityTypes.BLOCK_DISPLAY, area,
					ghost -> ghost.entityTags().contains(GHOST_TAG));
			ghosts.forEach(Entity::discard);
			player.sendOverlayMessage(Component.translatable("message.siteworks.ghost_test.removed", ghosts.size()));
		}
		return InteractionResult.SUCCESS;
	}

	/** A hollow 3x3x3 "house": stone brick floor, brick walls with glass windows, plank roof. */
	private static int spawnDemoHouse(ServerLevel level, BlockPos start) {
		int count = 0;
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				for (int z = 0; z < 3; z++) {
					if (x == 1 && y == 1 && z == 1) {
						continue; // hollow inside
					}
					BlockState state;
					if (y == 0) {
						state = Blocks.STONE_BRICKS.defaultBlockState();
					} else if (y == 2) {
						state = Blocks.OAK_PLANKS.defaultBlockState();
					} else if (x == 1 || z == 1) {
						state = Blocks.GLASS.defaultBlockState();
					} else {
						state = Blocks.BRICKS.defaultBlockState();
					}
					// Two "wrong" blocks, to see how the red glow looks.
					boolean wrong = (x == 0 && y == 1 && z == 0) || (x == 2 && y == 2 && z == 2);
					spawnGhost(level, start.offset(x - 1, y, z - 1), state, wrong ? RED : BLUE);
					count++;
				}
			}
		}
		return count;
	}

	/** 8x8x8 = 512 ghosts, the planned maximum for one projection. */
	private static int spawnBigCube(ServerLevel level, BlockPos start) {
		BlockState state = Blocks.STONE_BRICKS.defaultBlockState();
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				for (int z = 0; z < 8; z++) {
					spawnGhost(level, start.offset(x - 4, y, z - 4), state, BLUE);
				}
			}
		}
		return 8 * 8 * 8;
	}

	/** One ghost block: a Block Display at 80% size, centered in its cell, glowing, always fully lit. */
	private static void spawnGhost(ServerLevel level, BlockPos pos, BlockState state, int glowColor) {
		Display.BlockDisplay ghost = new Display.BlockDisplay(EntityTypes.BLOCK_DISPLAY, level);
		ghost.setBlockState(state);
		float offset = (1 - SCALE) / 2; // move it 0.1 in, so the smaller block sits in the middle of the cell
		ghost.setTransformation(new Transformation(
				new Vector3f(offset, offset, offset), new Quaternionf(),
				new Vector3f(SCALE, SCALE, SCALE), new Quaternionf()));
		ghost.setGlowingTag(true);
		ghost.setGlowColorOverride(glowColor);
		ghost.setBrightnessOverride(Brightness.FULL_BRIGHT);
		ghost.setPos(pos.getX(), pos.getY(), pos.getZ());
		ghost.addTag(GHOST_TAG);
		level.addFreshEntity(ghost);
	}
}
