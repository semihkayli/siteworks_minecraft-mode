package io.github.semihkayli.siteworks.registry;

import io.github.semihkayli.siteworks.SiteWorks;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/** Our tags. The lists of blocks are in data/siteworks/tags/. */
public final class ModTags {
	/** Blocks the Mattock digs in 3x3: dirt, sand, gravel, clay, mud... */
	public static final TagKey<Block> SOFT_GROUND = TagKey.create(Registries.BLOCK, SiteWorks.id("soft_ground"));

	/** Blocks the Sledgehammer breaks in 3x3: stone, bricks, concrete... but no ores. */
	public static final TagKey<Block> SLEDGE_BREAKABLE = TagKey.create(Registries.BLOCK, SiteWorks.id("sledge_breakable"));

	/** Blocks the Trowel places: bricks, stone bricks, cobblestone, concrete... Full blocks only. */
	public static final TagKey<Block> MASONRY_BLOCKS = TagKey.create(Registries.BLOCK, SiteWorks.id("masonry_blocks"));

	private ModTags() {
	}
}
