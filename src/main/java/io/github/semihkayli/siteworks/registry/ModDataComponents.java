package io.github.semihkayli.siteworks.registry;

import io.github.semihkayli.siteworks.SiteWorks;
import io.github.semihkayli.siteworks.item.ConcreteBucketItem;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
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

	private ModDataComponents() {
	}

	public static void init() {
	}
}
