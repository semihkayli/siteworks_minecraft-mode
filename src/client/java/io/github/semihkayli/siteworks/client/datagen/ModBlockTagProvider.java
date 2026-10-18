package io.github.semihkayli.siteworks.client.datagen;

import io.github.semihkayli.siteworks.registry.ModBlocks;
import io.github.semihkayli.siteworks.registry.ModTags;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

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
				.add(key(ModBlocks.CONCRETE), key(ModBlocks.REINFORCED_CONCRETE))
				.forceAddTag(BlockTags.BASE_STONE_OVERWORLD)
				.forceAddTag(BlockTags.BASE_STONE_NETHER)
				.forceAddTag(BlockTags.STONE_BRICKS)
				.forceAddTag(BlockTags.TERRACOTTA)
				.forceAddTag(BlockTags.CONCRETE);

		// Vanilla tool tags (data/minecraft/tags/block/*.json, "replace": false).
		// mineable/pickaxe = a pickaxe mines it fast. needs_iron_tool = drops only with an iron pickaxe or better.
		builder(BlockTags.MINEABLE_WITH_PICKAXE)
				.add(key(ModBlocks.CONCRETE), key(ModBlocks.REINFORCED_CONCRETE), key(ModBlocks.REBAR),
						key(ModBlocks.CONCRETE_MIXER), key(ModBlocks.STEEL_BEAM));
		// Like an iron block: a wooden pickaxe breaks it but gets nothing.
		builder(BlockTags.NEEDS_STONE_TOOL).add(key(ModBlocks.STEEL_BEAM));
		builder(BlockTags.NEEDS_IRON_TOOL).add(key(ModBlocks.REINFORCED_CONCRETE));
		builder(BlockTags.MINEABLE_WITH_AXE).add(key(ModBlocks.FORMWORK));
		builder(BlockTags.MINEABLE_WITH_SHOVEL).add(key(ModBlocks.WET_CONCRETE));
	}

	private static ResourceKey<Block> key(Block block) {
		return block.builtInRegistryHolder().key();
	}
}
