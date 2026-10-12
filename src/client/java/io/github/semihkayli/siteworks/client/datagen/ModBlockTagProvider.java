package io.github.semihkayli.siteworks.client.datagen;

import io.github.semihkayli.siteworks.registry.ModTags;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.BlockItemIds;
import net.minecraft.tags.BlockTags;

/** Writes data/siteworks/tags/block/*.json. */
public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
	public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		// forceAddTag = add a vanilla tag. Datagen cannot see vanilla tags, so plain addTag() fails its check.
		// Mattock: vanilla's shovel list, WITHOUT suspicious sand/gravel (protects archaeology finds).
		builder(ModTags.SOFT_GROUND)
				.add(BlockItemIds.GRASS_BLOCK, BlockItemIds.DIRT, BlockItemIds.COARSE_DIRT, BlockItemIds.ROOTED_DIRT,
						BlockItemIds.PODZOL, BlockItemIds.MYCELIUM, BlockItemIds.DIRT_PATH, BlockItemIds.FARMLAND,
						BlockItemIds.MUD, BlockItemIds.MUDDY_MANGROVE_ROOTS, BlockItemIds.SAND, BlockItemIds.RED_SAND,
						BlockItemIds.GRAVEL, BlockItemIds.CLAY, BlockItemIds.SOUL_SAND, BlockItemIds.SOUL_SOIL,
						BlockItemIds.SNOW_BLOCK, BlockItemIds.SNOW)
				.forceAddTag(BlockTags.CONCRETE_POWDERS);

		// Sledgehammer: stone-type blocks and masonry. No ores (they are simply not in the list).
		builder(ModTags.SLEDGE_BREAKABLE)
				.add(BlockItemIds.COBBLESTONE, BlockItemIds.MOSSY_COBBLESTONE, BlockItemIds.COBBLED_DEEPSLATE,
						BlockItemIds.SMOOTH_STONE, BlockItemIds.POLISHED_GRANITE, BlockItemIds.POLISHED_DIORITE,
						BlockItemIds.POLISHED_ANDESITE, BlockItemIds.POLISHED_DEEPSLATE, BlockItemIds.POLISHED_TUFF,
						BlockItemIds.DEEPSLATE_BRICKS, BlockItemIds.DEEPSLATE_TILES, BlockItemIds.TUFF_BRICKS,
						BlockItemIds.BRICKS, BlockItemIds.MUD_BRICKS, BlockItemIds.SANDSTONE, BlockItemIds.RED_SANDSTONE,
						BlockItemIds.CALCITE, BlockItemIds.DRIPSTONE_BLOCK, BlockItemIds.END_STONE,
						BlockItemIds.END_STONE_BRICKS, BlockItemIds.NETHER_BRICKS, BlockItemIds.POLISHED_BLACKSTONE,
						BlockItemIds.POLISHED_BLACKSTONE_BRICKS)
				.forceAddTag(BlockTags.BASE_STONE_OVERWORLD)
				.forceAddTag(BlockTags.BASE_STONE_NETHER)
				.forceAddTag(BlockTags.STONE_BRICKS)
				.forceAddTag(BlockTags.TERRACOTTA)
				.forceAddTag(BlockTags.CONCRETE);
	}
}
