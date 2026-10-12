package io.github.semihkayli.siteworks.event;

import io.github.semihkayli.siteworks.registry.ModItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * The Hard Hat cancels all damage from falling blocks (anvil, dripstone, and any falling block that deals damage).
 * Note: vanilla falling sand/gravel deal no damage at all (only suffocation after landing).
 * Vanilla already groups these damage types in the tag {@code #minecraft:damages_helmet}.
 */
public final class HardHatProtection {
	private HardHatProtection() {
	}

	public static void init() {
		// ALLOW_DAMAGE runs before any damage. Returning false cancels the damage.
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
			if (!head.is(ModItems.HARD_HAT) || !source.is(DamageTypeTags.DAMAGES_HELMET)) {
				return true;
			}
			// The hat still wears down, like vanilla armor: 1 durability per 4 damage, at least 1.
			head.hurtAndBreak(Math.max(1, (int) (amount / 4.0f)), entity, EquipmentSlot.HEAD);
			return false;
		});
	}
}
