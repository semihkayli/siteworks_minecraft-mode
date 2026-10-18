package io.github.semihkayli.siteworks.item;

import io.github.semihkayli.siteworks.block.WetConcreteBlock;
import io.github.semihkayli.siteworks.pour.MoldPour;
import io.github.semihkayli.siteworks.registry.ModBlocks;
import io.github.semihkayli.siteworks.registry.ModDataComponents;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
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
		BlockPos clicked = context.getClickedPos();
		// Clicked rebar or a plant inside the mold: pour right there. Clicked a wall or the floor: pour next to it.
		BlockPos start = cellOf(context.getLevel().getBlockState(clicked)) != MoldPour.Cell.SOLID
				? clicked
				: clicked.relative(context.getClickedFace());

		// The world is changed on the server only. The client just swings the arm.
		if (!(context.getLevel() instanceof ServerLevel level)) {
			return InteractionResult.SUCCESS;
		}
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		if (player != null && !player.mayUseItemAt(start, context.getClickedFace(), stack)) {
			return InteractionResult.FAIL;
		}

		MoldPour.Result result = MoldPour.plan((x, y, z) -> cellAt(level, player, new BlockPos(x, y, z)),
				new MoldPour.Pos(start.getX(), start.getY(), start.getZ()), getAmount(stack));

		for (MoldPour.Pos cell : result.cells()) {
			BlockPos pos = new BlockPos(cell.x(), cell.y(), cell.z());
			boolean rebar = level.getBlockState(pos).is(ModBlocks.REBAR);
			level.setBlock(pos, ModBlocks.WET_CONCRETE.defaultBlockState().setValue(WetConcreteBlock.REINFORCED, rebar),
					Block.UPDATE_ALL);
		}

		int poured = result.cells().size();
		if (poured > 0) {
			level.playSound(null, start, SoundEvents.MUD_PLACE, SoundSource.BLOCKS, 1.0f, 0.8f);
			level.playSound(null, start, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
		}
		if (player != null) {
			if (poured > 0) {
				player.awardStat(Stats.ITEM_USED.get(this));
			}
			if (result.status() == MoldPour.Status.LEAKS) {
				player.sendOverlayMessage(Component.translatable("message.siteworks.pour.leaks").withStyle(ChatFormatting.RED));
			} else if (result.status() == MoldPour.Status.FULL) {
				player.sendOverlayMessage(Component.translatable("message.siteworks.pour.full"));
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

	/** What a real block is for the pour. Protected blocks (spawn protection, world border) count as walls. */
	private static MoldPour.Cell cellAt(ServerLevel level, Player player, BlockPos pos) {
		if (level.isOutsideBuildHeight(pos) || (player != null && !level.mayInteract(player, pos))) {
			return MoldPour.Cell.SOLID;
		}
		return cellOf(level.getBlockState(pos));
	}

	private static MoldPour.Cell cellOf(BlockState state) {
		if (state.is(ModBlocks.REBAR)) {
			return MoldPour.Cell.REBAR;
		}
		// Air, water, lava, plants, snow layers... = blocks that a placed block would replace.
		return state.canBeReplaced() ? MoldPour.Cell.OPEN : MoldPour.Cell.SOLID;
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
