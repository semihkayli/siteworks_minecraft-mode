package io.github.semihkayli.siteworks.registry;

import io.github.semihkayli.siteworks.SiteWorks;
import io.github.semihkayli.siteworks.item.ConcreteBucketItem;
import io.github.semihkayli.siteworks.item.TrowelItem;
import io.github.semihkayli.siteworks.item.TrowelMode;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;

/**
 * Our data components: small pieces of data stored ON an item stack (saved with the world, sent to clients).
 */
public final class ModDataComponents {
	/** How many blocks of concrete are left in a Concrete Bucket (1-16). */
	public static final DataComponentType<Integer> CONCRETE_AMOUNT = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			SiteWorks.id("concrete_amount"),
			DataComponentType.<Integer>builder()
					.persistent(ExtraCodecs.intRange(1, ConcreteBucketItem.CAPACITY))
					.networkSynchronized(ByteBufCodecs.VAR_INT)
					.build());

	/** The Trowel's mode (Row / Course). */
	public static final DataComponentType<TrowelMode> TROWEL_MODE = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			SiteWorks.id("trowel_mode"),
			DataComponentType.<TrowelMode>builder()
					.persistent(TrowelMode.CODEC)
					.networkSynchronized(TrowelMode.STREAM_CODEC)
					.build());

	/** How many blocks the Trowel can still place with the mortar it already took (0-8). 1 Mortar = 8 blocks. */
	public static final DataComponentType<Integer> MORTAR_CHARGE = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			SiteWorks.id("mortar_charge"),
			DataComponentType.<Integer>builder()
					.persistent(ExtraCodecs.intRange(0, TrowelItem.BLOCKS_PER_MORTAR))
					.networkSynchronized(ByteBufCodecs.VAR_INT)
					.build());

	/** Row mode: the start SPOT of a "from -> to" row (1st click), with its dimension. Removed after the row is built. */
	public static final DataComponentType<GlobalPos> ROW_START = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			SiteWorks.id("row_start"),
			DataComponentType.<GlobalPos>builder()
					.persistent(GlobalPos.CODEC)
					.networkSynchronized(GlobalPos.STREAM_CODEC)
					.build());

	/** Row mode: the masonry block chosen at the 1st click (if there was one), e.g. the brick you clicked. */
	public static final DataComponentType<Item> ROW_BLOCK = Registry.register(
			BuiltInRegistries.DATA_COMPONENT_TYPE,
			SiteWorks.id("row_block"),
			DataComponentType.<Item>builder()
					.persistent(BuiltInRegistries.ITEM.byNameCodec())
					.networkSynchronized(ByteBufCodecs.registry(Registries.ITEM))
					.build());

	private ModDataComponents() {
	}

	public static void init() {
	}
}
