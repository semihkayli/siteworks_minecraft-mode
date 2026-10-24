package io.github.semihkayli.siteworks.item;

import io.github.semihkayli.siteworks.block.WetConcreteBlock;
import io.github.semihkayli.siteworks.foundation.Foundation;
import io.github.semihkayli.siteworks.foundation.FoundationLookup;
import io.github.semihkayli.siteworks.pour.MoldPour;
import io.github.semihkayli.siteworks.registry.ModBlocks;
import io.github.semihkayli.siteworks.registry.ModDataComponents;
import io.github.semihkayli.siteworks.util.Messages;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A bucket of fresh concrete. Right-click into a mold: the concrete fills it from the bottom up
 * (see {@link MoldPour}). The bucket remembers what's left, and becomes a normal empty bucket at 0.
 */
public class ConcreteBucketItem extends Item {
	/** One full bucket = 16 blocks. */
	public static final int CAPACITY = 16;
	private static final int BAR_COLOR = 0xA0A0A0;

	public ConcreteBucketItem(Properties properties) {
		super(properties);
	}

	public static int getAmount(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.CONCRETE_AMOUNT, CAPACITY);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		BlockPos start = startFor(context.getLevel(), context.getClickedPos(), context.getClickedFace());

		// The world is changed on the server only. The client just swings the arm.
		if (!(context.getLevel() instanceof ServerLevel level)) {
			return InteractionResult.SUCCESS;
		}
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		if (player != null && !player.mayUseItemAt(start, context.getClickedFace(), stack)) {
			return InteractionResult.FAIL;
		}

		MoldPour.Result result = plan(level, player, start, getAmount(stack));
		Allowed allowed = allowedCells(level, player, result.cells());

		for (MoldPour.Pos cell : allowed.cells()) {
			BlockPos pos = new BlockPos(cell.x(), cell.y(), cell.z());
			boolean rebar = level.getBlockState(pos).is(ModBlocks.REBAR);
			level.setBlock(pos, ModBlocks.WET_CONCRETE.defaultBlockState().setValue(WetConcreteBlock.REINFORCED, rebar),
					Block.UPDATE_ALL);
		}

		int poured = allowed.cells().size();
		if (poured > 0) {
			level.playSound(null, start, SoundEvents.MUD_PLACE, SoundSource.BLOCKS, 1.0f, 0.8f);
			level.playSound(null, start, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
		}
		if (player != null) {
			if (poured > 0) {
				player.awardStat(Stats.ITEM_USED.get(this));
			}
			if (allowed.limit() != null) {
				Messages.error(player, FoundationLookup.tooTallMessage(allowed.limit()));
			} else switch (result.status()) {
				case LEAKS -> Messages.error(player, Component.translatable("message.siteworks.pour.leaks"));
				case PROTECTED -> Messages.error(player, Component.translatable("message.siteworks.pour.protected"));
				case NO_FORMWORK -> Messages.error(player, Component.translatable("message.siteworks.pour.no_formwork"));
				case FULL -> player.sendOverlayMessage(Component.translatable("message.siteworks.pour.full"));
				case POURED -> {
				}
			}
			// Like vanilla buckets: in creative mode the bucket stays full.
			if (!player.hasInfiniteMaterials() && poured > 0) {
				int left = getAmount(stack) - poured;
				if (left > 0) {
					stack.set(ModDataComponents.CONCRETE_AMOUNT, left);
				} else {
					player.setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
				}
			}
		}
		return InteractionResult.SUCCESS;
	}

	/**
	 * Where a pour starts. Clicked rebar or a plant inside the mold: right there. Clicked a wall, the floor
	 * or concrete: the cell next to the clicked face. Also used by the client preview (PourPreview), so both agree.
	 */
	public static BlockPos startFor(Level level, BlockPos clicked, Direction face) {
		MoldPour.Cell cell = cellOf(level.getBlockState(clicked));
		return cell == MoldPour.Cell.OPEN || cell == MoldPour.Cell.REBAR ? clicked : clicked.relative(face);
	}

	/**
	 * The pour cells that may really be placed: all of them, or only the ones before the first cell that would make
	 * a building on a foundation too tall (the hard stop, Phase 3¾). {@code refused} / {@code limit} = that cell and
	 * its foundation, or null.
	 */
	public record Allowed(List<MoldPour.Pos> cells, MoldPour.Pos refused, Foundation.Stack limit) {
	}

	/** The foundation hard stop for a pour. The cells are in placing order, so the ones before the cut are still safe. */
	public static Allowed allowedCells(Level level, Player player, List<MoldPour.Pos> cells) {
		for (int i = 0; i < cells.size(); i++) {
			MoldPour.Pos cell = cells.get(i);
			Foundation.Stack limit = FoundationLookup.refusedFor(player, level, new BlockPos(cell.x(), cell.y(), cell.z()));
			if (limit != null) {
				return new Allowed(cells.subList(0, i), cell, limit);
			}
		}
		return new Allowed(cells, null, null);
	}

	/** Plans the pour on the real blocks. Also used by the client preview (with the client's copy of the world). */
	public static MoldPour.Result plan(Level level, Player player, BlockPos start, int amount) {
		return MoldPour.plan((x, y, z) -> cellAt(level, player, new BlockPos(x, y, z)),
				new MoldPour.Pos(start.getX(), start.getY(), start.getZ()), amount);
	}

	/**
	 * What a real block is for the pour. A protected spot (spawn protection, world border, outside the world)
	 * stays a wall if there is a block, but an open protected spot is BLOCKED: concrete may not flow into it.
	 */
	private static MoldPour.Cell cellAt(Level level, Player player, BlockPos pos) {
		MoldPour.Cell cell = cellOf(level.getBlockState(pos));
		boolean isProtected = level.isOutsideBuildHeight(pos) || (player != null && !level.mayInteract(player, pos));
		if (isProtected && (cell == MoldPour.Cell.OPEN || cell == MoldPour.Cell.REBAR)) {
			return MoldPour.Cell.BLOCKED;
		}
		return cell;
	}

	private static MoldPour.Cell cellOf(BlockState state) {
		if (state.is(ModBlocks.REBAR)) {
			return MoldPour.Cell.REBAR;
		}
		if (state.is(ModBlocks.WET_CONCRETE) || state.is(ModBlocks.CONCRETE) || state.is(ModBlocks.REINFORCED_CONCRETE)) {
			return MoldPour.Cell.CONCRETE;
		}
		// Good mold sides: Formwork, and a Steel Beam standing in the mold.
		if (state.is(ModBlocks.FORMWORK) || state.is(ModBlocks.STEEL_BEAM)) {
			return MoldPour.Cell.FORMWORK;
		}
		// Air, water, lava, plants, snow layers... = blocks that a placed block would replace.
		return state.canBeReplaced() ? MoldPour.Cell.OPEN : MoldPour.Cell.SOLID;
	}

	/** The amount changes after every pour. Without this, the hand would dip (the "take the item out again" animation). */
	@Override
	public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
		return false;
	}

	// The bar under the icon (like durability) shows what's left. Hidden when the bucket is full.
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getAmount(stack) < CAPACITY;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round(13.0f * getAmount(stack) / CAPACITY);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return BAR_COLOR;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
			Consumer<Component> tooltip, TooltipFlag flag) {
		tooltip.accept(Component.translatable("item.siteworks.concrete_bucket.amount", getAmount(stack), CAPACITY)
				.withStyle(ChatFormatting.GRAY));
	}
}
