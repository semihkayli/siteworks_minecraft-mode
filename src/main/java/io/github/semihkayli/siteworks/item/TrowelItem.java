package io.github.semihkayli.siteworks.item;

import io.github.semihkayli.siteworks.registry.ModDataComponents;
import io.github.semihkayli.siteworks.registry.ModItems;
import io.github.semihkayli.siteworks.registry.ModTags;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
 * The Trowel (mala) = a builder's wand for masonry. Right-click a masonry block: it places many blocks of the same
 * kind at once, from your inventory, and uses Mortar (1 Mortar = 8 blocks). Sneak + right-click the air = next mode.
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

	/** Sneak + right-click the air: switch to the next mode. */
	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (!player.isShiftKeyDown()) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide()) {
			ItemStack stack = player.getItemInHand(hand);
			TrowelMode mode = getMode(stack).next();
			stack.set(ModDataComponents.TROWEL_MODE, mode);
			player.sendOverlayMessage(Component.translatable("message.siteworks.trowel.mode", mode.displayName()));
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
		TrowelMode mode = getMode(trowel);
		if (mode == TrowelMode.COURSE) {
			// TEMPORARY until Phase 3 step 4.
			player.sendOverlayMessage(Component.translatable("message.siteworks.trowel.course_soon"));
			return InteractionResult.FAIL;
		}
		// Adventure mode can't build.
		if (!player.getAbilities().mayBuild) {
			return InteractionResult.FAIL;
		}

		BlockPos clicked = context.getClickedPos();
		BlockState clickedState = level.getBlockState(clicked);
		BlockItem blockItem = pickBlock(player, context.getHand(), clickedState);
		if (blockItem == null) {
			message(player, Component.translatable("message.siteworks.trowel.not_masonry"));
			return InteractionResult.FAIL;
		}

		// Row: a straight line out from the clicked face. (If you click grass or snow, the line starts in it.)
		Direction face = context.getClickedFace();
		BlockPos first = clickedState.canBeReplaced() ? clicked : clicked.relative(face);
		boolean creative = player.hasInfiniteMaterials();
		int charge = getCharge(trowel);
		int placed = 0;
		Component problem = null;
		for (int i = 0; i < mode.maxBlocks(); i++) {
			BlockPos pos = first.relative(face, i);
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
					new BlockPlaceContext(player, context.getHand(), blocks,
							new BlockHitResult(Vec3.atCenterOf(pos), face, pos, false)),
					pos, face);
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
		if (placed > 0) {
			player.awardStat(Stats.ITEM_USED.get(this));
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	/**
	 * Which block to place: a masonry block in your OTHER hand wins. Otherwise the block you clicked,
	 * if it is masonry. Null = nothing the trowel can place.
	 */
	private static BlockItem pickBlock(Player player, InteractionHand hand, BlockState clicked) {
		InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		if (player.getItemInHand(other).getItem() instanceof BlockItem held
				&& held.getBlock().defaultBlockState().is(ModTags.MASONRY_BLOCKS)) {
			return held;
		}
		if (clicked.is(ModTags.MASONRY_BLOCKS) && clicked.getBlock().asItem() instanceof BlockItem item) {
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
		tooltip.accept(Component.translatable("item.siteworks.trowel.hint").withStyle(ChatFormatting.DARK_GRAY));
	}
}
