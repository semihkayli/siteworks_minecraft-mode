package io.github.semihkayli.siteworks.block.entity;

import io.github.semihkayli.siteworks.block.ConcreteMixerBlock;
import io.github.semihkayli.siteworks.item.ConcreteBucketItem;
import io.github.semihkayli.siteworks.registry.ModBlockEntities;
import io.github.semihkayli.siteworks.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * The data and the "brain" of the Concrete Mixer: stored ingredients, water, ready concrete, mixing progress.
 * A block entity (blok varlığı) is extra data + logic attached to one block; it's saved with the world.
 *
 * One batch = 1 cement + 4 sand + 4 gravel + 1 water -> 16 blocks of concrete, in 5 seconds.
 * Hoppers can feed the ingredients from any side (WorldlyContainer), but can't take anything out.
 */
public class ConcreteMixerBlockEntity extends BlockEntity implements WorldlyContainer {
	public static final int SLOT_CEMENT = 0;
	public static final int SLOT_SAND = 1;
	public static final int SLOT_GRAVEL = 2;
	/** Max items in each ingredient slot. */
	public static final int SLOT_LIMIT = 64;

	public static final int CEMENT_PER_BATCH = 1;
	public static final int SAND_PER_BATCH = 4;
	public static final int GRAVEL_PER_BATCH = 4;
	/** One batch = one full Concrete Bucket. */
	public static final int BLOCKS_PER_BATCH = ConcreteBucketItem.CAPACITY;
	/** A water bucket = water for 8 batches. */
	public static final int MAX_WATER = 8;
	/** Max ready concrete, in blocks (4 buckets). */
	public static final int MAX_READY = 64;
	/** 5 seconds per batch. */
	public static final int MIX_TICKS = 100;

	private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
	private int water;
	private int ready;
	private int progress;

	public ConcreteMixerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CONCRETE_MIXER, pos, state);
	}

	// ---- Mixing (runs every tick on the server) ----

	public static void serverTick(Level level, BlockPos pos, BlockState state, ConcreteMixerBlockEntity mixer) {
		// A water source next to the mixer refills it by itself (checked once per second).
		if (mixer.water < MAX_WATER && level.getGameTime() % 20 == 0 && hasWaterSourceNextTo(level, pos)) {
			mixer.water = MAX_WATER;
			mixer.setChanged();
		}

		if (!mixer.canMix()) {
			if (mixer.progress > 0) {
				mixer.progress = 0;
				mixer.setChanged();
			}
			return;
		}

		mixer.progress++;
		if (level instanceof ServerLevel serverLevel && mixer.progress % 10 == 0) {
			serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CONCRETE_POWDER.gray().defaultBlockState()),
					pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 4, 0.25, 0.05, 0.25, 0.0);
		}
		if (mixer.progress % 40 == 1) {
			level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.5f, 0.6f);
		}
		if (mixer.progress >= MIX_TICKS) {
			mixer.items.get(SLOT_CEMENT).shrink(CEMENT_PER_BATCH);
			mixer.items.get(SLOT_SAND).shrink(SAND_PER_BATCH);
			mixer.items.get(SLOT_GRAVEL).shrink(GRAVEL_PER_BATCH);
			mixer.water--;
			mixer.ready += BLOCKS_PER_BATCH;
			mixer.progress = 0;
			level.playSound(null, pos, SoundEvents.MUD_PLACE, SoundSource.BLOCKS, 1.0f, 0.8f);
			mixer.updateFillLevel();
		}
		mixer.setChanged();
	}

	private boolean canMix() {
		return items.get(SLOT_CEMENT).getCount() >= CEMENT_PER_BATCH
				&& items.get(SLOT_SAND).getCount() >= SAND_PER_BATCH
				&& items.get(SLOT_GRAVEL).getCount() >= GRAVEL_PER_BATCH
				&& water >= 1
				&& ready + BLOCKS_PER_BATCH <= MAX_READY;
	}

	public boolean isMixing() {
		return progress > 0;
	}

	private static boolean hasWaterSourceNextTo(Level level, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (level.getFluidState(pos.relative(direction)).isSourceOfType(Fluids.WATER)) {
				return true;
			}
		}
		return false;
	}

	/** The block shows the ready concrete inside: 0 = empty, 1-3 = a third / two thirds / full. */
	private void updateFillLevel() {
		if (level == null) {
			return;
		}
		int fill = (ready * 3 + MAX_READY - 1) / MAX_READY;
		BlockState state = getBlockState();
		if (state.getValue(ConcreteMixerBlock.FILL) != fill) {
			level.setBlock(worldPosition, state.setValue(ConcreteMixerBlock.FILL, fill), Block.UPDATE_CLIENTS);
		}
	}

	// ---- Right-click actions (called by ConcreteMixerBlock) ----

	/** Which ingredient slot accepts this item, or -1. */
	public static int slotFor(ItemStack stack) {
		if (stack.is(ModItems.CEMENT)) {
			return SLOT_CEMENT;
		}
		if (stack.is(Items.SAND) || stack.is(Items.RED_SAND)) {
			return SLOT_SAND;
		}
		if (stack.is(Items.GRAVEL)) {
			return SLOT_GRAVEL;
		}
		return -1;
	}

	/**
	 * Puts as much of the stack as fits into its slot. Returns how many items went in.
	 * A slot holds one item type: sand and red sand can't be mixed in the sand slot (empty it first).
	 */
	public int insert(int slot, ItemStack stack, boolean consume) {
		ItemStack stored = items.get(slot);
		if (!stored.isEmpty() && !ItemStack.isSameItemSameComponents(stored, stack)) {
			return 0;
		}
		int amount = Math.min(SLOT_LIMIT - stored.getCount(), stack.getCount());
		if (amount <= 0) {
			return 0;
		}
		if (stored.isEmpty()) {
			items.set(slot, stack.copyWithCount(amount));
		} else {
			stored.grow(amount);
		}
		if (consume) {
			stack.shrink(amount);
		}
		setChanged();
		return amount;
	}

	/** A water bucket fills the tank. Returns false if it's already full. */
	public boolean addWater() {
		if (water >= MAX_WATER) {
			return false;
		}
		water = MAX_WATER;
		setChanged();
		return true;
	}

	/** Takes up to {@code max} blocks of ready concrete. Returns how many were taken. */
	public int takeConcrete(int max) {
		int taken = Math.min(max, ready);
		if (taken > 0) {
			ready -= taken;
			setChanged();
			updateFillLevel();
		}
		return taken;
	}

	public Component statusMessage() {
		Component status = Component.translatable("message.siteworks.mixer.status",
				items.get(SLOT_CEMENT).getCount(), items.get(SLOT_SAND).getCount(), items.get(SLOT_GRAVEL).getCount(),
				water, MAX_WATER, ready, MAX_READY);
		return isMixing() ? status.copy().append(Component.translatable("message.siteworks.mixer.mixing")) : status;
	}

	// ---- Hoppers (WorldlyContainer) ----
	// Hoppers (and droppers, hopper minecarts...) see the 3 ingredient slots from every side.
	// Each item can only go into its own slot, and nothing can be taken out (no hopper output in v1.0).

	private static final int[] ALL_SLOTS = {SLOT_CEMENT, SLOT_SAND, SLOT_GRAVEL};

	@Override
	public int[] getSlotsForFace(Direction side) {
		return ALL_SLOTS;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slotFor(stack) == slot;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
		return canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		return false;
	}

	@Override
	public int getContainerSize() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		return items.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getItem(int slot) {
		return items.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack removed = ContainerHelper.removeItem(items, slot, count);
		if (!removed.isEmpty()) {
			setChanged();
		}
		return removed;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ContainerHelper.takeItem(items, slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		items.set(slot, stack);
		stack.limitSize(getMaxStackSize(stack));
		setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this, player);
	}

	@Override
	public void clearContent() {
		items.clear();
	}

	// ---- Saving ----

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, items);
		output.putInt("water", water);
		output.putInt("ready", ready);
		output.putInt("progress", progress);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		items.clear();
		ContainerHelper.loadAllItems(input, items);
		water = input.getIntOr("water", 0);
		ready = input.getIntOr("ready", 0);
		progress = input.getIntOr("progress", 0);
	}

	/** The mixer is broken: drop the stored ingredients. Water and ready concrete are lost (they have no item). */
	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level != null) {
			Containers.dropContents(level, pos, items);
		}
	}
}
