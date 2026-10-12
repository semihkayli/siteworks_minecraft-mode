package io.github.semihkayli.siteworks.client.datagen;

import io.github.semihkayli.siteworks.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

/**
 * Writes item tags. We add our items to VANILLA tags (data/minecraft/tags/item/*.json, "replace": false),
 * because vanilla enchantments only accept items from these tags. Example: Efficiency accepts
 * #enchantable/mining = #pickaxes, #shovels, ... Without this, our tools can't be enchanted at all.
 */
public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
	public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		// Mattock = a shovel: Efficiency, Fortune, Silk Touch, Unbreaking, Mending.
		builder(ItemTags.SHOVELS).add(key(ModItems.MATTOCK));
		// Sledgehammer = a pickaxe: the same mining enchantments.
		builder(ItemTags.PICKAXES).add(key(ModItems.SLEDGEHAMMER));
		// Hard Hat = a helmet: Protection, Respiration, Aqua Affinity, Unbreaking, Mending.
		builder(ItemTags.HEAD_ARMOR).add(key(ModItems.HARD_HAT));
	}

	private static ResourceKey<Item> key(Item item) {
		return item.builtInRegistryHolder().key();
	}
}
