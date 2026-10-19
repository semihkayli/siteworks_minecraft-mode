package io.github.semihkayli.siteworks.item;

import io.github.semihkayli.siteworks.registry.ModDataComponents;
import io.github.semihkayli.siteworks.registry.ModItems;
import io.github.semihkayli.siteworks.registry.ModTags;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * The Trowel (mala) = a builder's wand for masonry. It places many blocks at once, from your inventory,
 * and uses Mortar (1 Mortar = 8 blocks). Sneak + right-click the air = next mode.
 * Row mode is "from -> to" (Phase 3 step 3b): 1st click = where the row starts, 2nd click = where it ends (both included).
 * Every block is placed by vanilla's BlockItem.place(), so all vanilla rules apply (entities, sounds, creative...).
 */
public class TrowelItem extends Item {
	/** 1 Mortar = 8 blocks. The rest is kept in the trowel (MORTAR_CHARGE), so nothing is wasted. */
	public static final int BLOCKS_PER_MORTAR = 8;
	private static final int BAR_COLOR = 0xC8B89A; // light beige, like mortar

	public TrowelItem(Properties properties) {
		super(properties);
	}

	public static TrowelMode getMode(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.TROWEL_MODE, TrowelMode.ROW);
	}

	public static int getCharge(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.MORTAR_CHARGE, 0);
	}

	/**
	 * Sneak + right-click the air: if a row start is set, remove it (cancel).
	 * Otherwise switch to the next mode.
	 */
	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (!player.isShiftKeyDown()) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide()) {
			ItemStack stack = player.getItemInHand(hand);
			if (stack.has(ModDataComponents.ROW_START)) {
				clearRowStart(stack);
				player.sendOverlayMessage(Component.translatable("message.siteworks.trowel.row_cancel"));
			} else {
				TrowelMode mode = getMode(stack).next();
				stack.set(ModDataComponents.TROWEL_MODE, mode);
				player.sendOverlayMessage(Component.translatable("message.siteworks.trowel.mode", mode.displayName()));
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		// The world is changed on the server only. The client just swings the arm.
		if (!(context.getLevel() instanceof ServerLevel level) || context.getPlayer() == null) {
			return InteractionResult.SUCCESS;
		}
		Player player = context.getPlayer();
		ItemStack trowel = context.getItemInHand();
		if (getMode(trowel) == TrowelMode.COURSE) {
			// TEMPORARY until Phase 3 step 4.
			player.sendOverlayMessage(Component.translatable("message.siteworks.trowel.course_soon"));
			return InteractionResult.FAIL;
		}
		// Adventure mode can't build.
		if (!player.getAbilities().mayBuild) {
			return InteractionResult.FAIL;
		}

		// Both clicks mark a SPOT where a brick goes: in front of the clicked face (or in it, for grass and snow).
		BlockPos clicked = context.getClickedPos();
		BlockState clickedState = level.getBlockState(clicked);
		Direction face = context.getClickedFace();
		BlockPos spot = clickedState.canBeReplaced() ? clicked : clicked.relative(face);
		InteractionHand hand = context.getHand();
		GlobalPos start = trowel.get(ModDataComponents.ROW_START);

		// 1st click (or the start is in another dimension): remember the start spot.
		if (start == null || !start.dimension().equals(level.dimension())) {
			setRowStart(level, player, hand, trowel, spot, clickedState,
					Component.translatable("message.siteworks.trowel.row_start", TrowelMode.ROW.maxBlocks()), false);
			return InteractionResult.SUCCESS;
		}

		// 2nd click: the row goes from the start spot to this spot, both included.
		BlockPos from = start.pos();
		Direction direction = spot.equals(from) ? face.getOpposite() : straightDirection(from, spot);
		int length = from.distManhattan(spot) + 1;
		// A wrong 2nd click never gets you stuck: it simply becomes the new start.
		if (direction == null) {
			setRowStart(level, player, hand, trowel, spot, clickedState,
					Component.translatable("message.siteworks.trowel.not_straight"), true);
			return InteractionResult.FAIL;
		}
		if (length > TrowelMode.ROW.maxBlocks()) {
			setRowStart(level, player, hand, trowel, spot, clickedState,
					Component.translatable("message.siteworks.trowel.too_long", TrowelMode.ROW.maxBlocks()), true);
			return InteractionResult.FAIL;
		}

		// Which brick: the other hand, else the masonry block of the 1st click, else of this click.
		BlockItem blockItem = otherHandBlock(player, hand);
		if (blockItem == null && trowel.get(ModDataComponents.ROW_BLOCK) instanceof BlockItem first) {
			blockItem = first;
		}
		if (blockItem == null) {
			blockItem = masonryBlock(clickedState);
		}
		clearRowStart(trowel);
		if (blockItem == null) {
			message(player, Component.translatable("message.siteworks.trowel.need_block"));
			return InteractionResult.FAIL;
		}

		int placed = placeLine(level, player, hand, trowel, blockItem, from, direction, length);
		if (placed > 0) {
			player.awardStat(Stats.ITEM_USED.get(this));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	/**
	 * Saves the row start spot (+ the masonry block of this click, if any), shows green sparkles there and a message.
	 * {@code warning} = the message is red (a wrong 2nd click that became the new start).
	 */
	private static void setRowStart(ServerLevel level, Player player, InteractionHand hand, ItemStack trowel,
			BlockPos spot, BlockState clickedState, Component text, boolean warning) {
		trowel.set(ModDataComponents.ROW_START, GlobalPos.of(level.dimension(), spot));
		BlockItem block = otherHandBlock(player, hand);
		if (block == null) {
			block = masonryBlock(clickedState);
		}
		if (block != null) {
			trowel.set(ModDataComponents.ROW_BLOCK, block);
		} else {
			trowel.remove(ModDataComponents.ROW_BLOCK);
		}
		level.sendParticles(ParticleTypes.HAPPY_VILLAGER, spot.getX() + 0.5, spot.getY() + 0.5, spot.getZ() + 0.5,
				10, 0.25, 0.25, 0.25, 0.0);
		if (warning) {
			message(player, text);
		} else {
			player.sendOverlayMessage(text);
		}
	}

	private static void clearRowStart(ItemStack trowel) {
		trowel.remove(ModDataComponents.ROW_START);
		trowel.remove(ModDataComponents.ROW_BLOCK);
	}

	/**
	 * Places up to {@code count} blocks in a line starting AT {@code from}, going {@code direction}.
	 * Stops at the first blocked spot, or when blocks or mortar run out. Returns how many were placed.
	 */
	private static int placeLine(ServerLevel level, Player player, InteractionHand hand, ItemStack trowel,
			BlockItem blockItem, BlockPos from, Direction direction, int count) {
		boolean creative = player.hasInfiniteMaterials();
		int charge = getCharge(trowel);
		int placed = 0;
		Component problem = null;
		for (int i = 0; i < count; i++) {
			BlockPos pos = from.relative(direction, i);
			// The first blocked spot ends the row: a block, the build height, spawn protection, the world border.
			if (!level.isInWorldBounds(pos) || !level.mayInteract(player, pos) || !level.getBlockState(pos).canBeReplaced()) {
				if (placed == 0) {
					problem = Component.translatable("message.siteworks.trowel.blocked");
				}
				break;
			}
			ItemStack blocks = creative ? new ItemStack(blockItem) : findStack(player.getInventory(), blockItem);
			if (blocks.isEmpty()) {
				problem = Component.translatable("message.siteworks.trowel.no_blocks", blockItem.getBlock().getName());
				break;
			}
			// Take a new Mortar only when the charge is empty AND a block can really go here.
			if (!creative && charge == 0) {
				ItemStack mortar = findStack(player.getInventory(), ModItems.MORTAR);
				if (mortar.isEmpty()) {
					problem = Component.translatable("message.siteworks.trowel.no_mortar");
					break;
				}
				mortar.shrink(1);
				charge = BLOCKS_PER_MORTAR;
			}
			// Place it like a normal right-click on the spot: vanilla checks entities, plays the sound,
			// and uses up 1 block from the stack (not in creative).
			BlockPlaceContext placeContext = BlockPlaceContext.at(
					new BlockPlaceContext(player, hand, blocks, new BlockHitResult(Vec3.atCenterOf(pos), direction, pos, false)),
					pos, direction);
			if (!blockItem.place(placeContext).consumesAction()) {
				// A player or a mob stands there. The mortar charge stays in the trowel.
				if (placed == 0) {
					problem = Component.translatable("message.siteworks.trowel.blocked");
				}
				break;
			}
			placed++;
			if (!creative) {
				charge--;
			}
		}
		trowel.set(ModDataComponents.MORTAR_CHARGE, charge);
		if (problem != null) {
			message(player, problem);
		}
		return placed;
	}

	/** The direction from {@code from} to {@code to} if they are on one straight line (only 1 coordinate differs), else null. */
	private static Direction straightDirection(BlockPos from, BlockPos to) {
		int dx = to.getX() - from.getX();
		int dy = to.getY() - from.getY();
		int dz = to.getZ() - from.getZ();
		int nonZero = (dx != 0 ? 1 : 0) + (dy != 0 ? 1 : 0) + (dz != 0 ? 1 : 0);
		if (nonZero != 1) {
			return null;
		}
		Direction.Axis axis = dx != 0 ? Direction.Axis.X : dy != 0 ? Direction.Axis.Y : Direction.Axis.Z;
		int delta = dx + dy + dz;
		return Direction.fromAxisAndDirection(axis, delta > 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
	}

	/** A masonry block in the hand that does NOT hold the trowel, or null. */
	private static BlockItem otherHandBlock(Player player, InteractionHand hand) {
		InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		if (player.getItemInHand(other).getItem() instanceof BlockItem held
				&& held.getBlock().defaultBlockState().is(ModTags.MASONRY_BLOCKS)) {
			return held;
		}
		return null;
	}

	/** The item of a clicked block if it is masonry (copy the wall), or null. */
	private static BlockItem masonryBlock(BlockState state) {
		if (state.is(ModTags.MASONRY_BLOCKS) && state.getBlock().asItem() instanceof BlockItem item) {
			return item;
		}
		return null;
	}

	/** The first stack of this item anywhere in the inventory (hotbar, main, off hand), or an empty stack. */
	private static ItemStack findStack(Inventory inventory, Item item) {
		for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (stack.is(item)) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	private static void message(Player player, Component text) {
		player.sendOverlayMessage(text.copy().withStyle(ChatFormatting.RED));
	}

	// The bar under the icon shows the mortar left in the trowel (0-8 blocks). Hidden when empty.
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getCharge(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(13.0f * getCharge(stack) / BLOCKS_PER_MORTAR);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return BAR_COLOR;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
			Consumer<Component> tooltip, TooltipFlag flag) {
		tooltip.accept(Component.translatable("item.siteworks.trowel.mode_line", getMode(stack).displayName())
				.withStyle(ChatFormatting.GRAY));
		tooltip.accept(Component.translatable("item.siteworks.trowel.charge", getCharge(stack), BLOCKS_PER_MORTAR)
				.withStyle(ChatFormatting.GRAY));
		GlobalPos start = stack.get(ModDataComponents.ROW_START);
		if (start != null) {
			tooltip.accept(Component.translatable("item.siteworks.trowel.row_start_line",
					start.pos().getX(), start.pos().getY(), start.pos().getZ()).withStyle(ChatFormatting.YELLOW));
		}
		if (getMode(stack) == TrowelMode.ROW) {
			tooltip.accept(Component.translatable("item.siteworks.trowel.row_hint").withStyle(ChatFormatting.DARK_GRAY));
		}
		tooltip.accept(Component.translatable("item.siteworks.trowel.hint").withStyle(ChatFormatting.DARK_GRAY));
	}
}
