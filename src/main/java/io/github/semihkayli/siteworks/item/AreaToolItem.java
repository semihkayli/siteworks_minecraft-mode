package io.github.semihkayli.siteworks.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * A tool that breaks a 3x3 area (Mattock, Sledgehammer).
 * The extra blocks around the center break only if they are in {@code areaBlocks}.
 * The digging logic is in {@link io.github.semihkayli.siteworks.event.AreaMining}.
 */
public class AreaToolItem extends Item {
	private final TagKey<Block> areaBlocks;

	public AreaToolItem(TagKey<Block> areaBlocks, Properties properties) {
		super(properties);
		this.areaBlocks = areaBlocks;
	}

	public TagKey<Block> areaBlocks() {
		return areaBlocks;
	}
}
