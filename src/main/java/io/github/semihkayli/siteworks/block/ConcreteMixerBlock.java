package io.github.semihkayli.siteworks.block;

import io.github.semihkayli.siteworks.block.entity.ConcreteMixerBlockEntity;
import io.github.semihkayli.siteworks.item.ConcreteBucketItem;
import io.github.semihkayli.siteworks.registry.ModBlockEntities;
import io.github.semihkayli.siteworks.registry.ModDataComponents;
import io.github.semihkayli.siteworks.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Concrete Mixer (betoniyer). No GUI: right-click it with ingredients (composter-style).
 * <ul>
 *   <li>Cement, sand (or red sand), gravel: the whole held stack goes in (up to 64 each).</li>
 *   <li>Water bucket: water for 8 batches. A water source next to the mixer refills it by itself.</li>
 *   <li>Empty bucket: takes up to 16 blocks of ready concrete -> Concrete Bucket.</li>
 *   <li>Half-full Concrete Bucket: tops it up.</li>
 *   <li>Empty hand: shows what's inside in the action bar.</li>
 * </ul>
 * Sneak + right-click with an item = vanilla behaviour (e.g. place sand next to the mixer).
 */
public class ConcreteMixerBlock extends BaseEntityBlock {
	/** How much ready concrete is shown inside: 0 = empty, 3 = full. */
	public static final IntegerProperty FILL = IntegerProperty.create("fill", 0, 3);

	public ConcreteMixerBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FILL, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FILL);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ConcreteMixerBlockEntity(pos, state);
	}

	/** Mixing runs on the server only. The client sees the result through the FILL state, sounds and particles. */
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.CONCRETE_MIXER, ConcreteMixerBlockEntity::serverTick);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		boolean known = ConcreteMixerBlockEntity.slotFor(stack) >= 0 || stack.is(Items.WATER_BUCKET)
				|| stack.is(Items.BUCKET) || stack.is(ModItems.CONCRETE_BUCKET);
		if (!known) {
			return InteractionResult.TRY_WITH_EMPTY_HAND; // other items: the empty-hand action (show the status)
		}
		// The client can't see the stored data, so it only swings the arm. The server does the real work.
		if (level.isClientSide() || !(level.getBlockEntity(pos) instanceof ConcreteMixerBlockEntity mixer)) {
			return InteractionResult.SUCCESS;
		}
		boolean consume = !player.hasInfiniteMaterials();

		int slot = ConcreteMixerBlockEntity.slotFor(stack);
		if (slot >= 0) {
			if (mixer.insert(slot, stack, consume) > 0) {
				level.playSound(null, pos, SoundEvents.GRAVEL_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
			}
		} else if (stack.is(Items.WATER_BUCKET)) {
			if (mixer.addWater()) {
				level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
			}
		} else if (stack.is(Items.BUCKET)) {
			int taken = mixer.takeConcrete(ConcreteBucketItem.CAPACITY);
			if (taken == 0) {
				player.sendOverlayMessage(Component.translatable("message.siteworks.mixer.no_concrete"));
				return InteractionResult.SUCCESS;
			}
			ItemStack filled = new ItemStack(ModItems.CONCRETE_BUCKET);
			filled.set(ModDataComponents.CONCRETE_AMOUNT, taken);
			level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 0.8f);
			player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, filled));
		} else {
			// A Concrete Bucket: top it up to 16.
			int amount = ConcreteBucketItem.getAmount(stack);
			int taken = mixer.takeConcrete(ConcreteBucketItem.CAPACITY - amount);
			if (taken > 0) {
				stack.set(ModDataComponents.CONCRETE_AMOUNT, amount + taken);
				level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 0.8f);
			}
		}
		player.sendOverlayMessage(mixer.statusMessage());
		return InteractionResult.SUCCESS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ConcreteMixerBlockEntity mixer) {
			player.sendOverlayMessage(mixer.statusMessage());
		}
		return InteractionResult.SUCCESS;
	}
}
