package io.github.semihkayli.siteworks.registry;

import io.github.semihkayli.siteworks.SiteWorks;
import io.github.semihkayli.siteworks.block.RebarBlock;
import io.github.semihkayli.siteworks.block.SafetyNetBlock;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

/** All blocks of the mod. Each block also gets a BlockItem, so it can be held and placed. */
public final class ModBlocks {
	// Soft like wool, burns, no fall damage, small bounce (slime = 1.0, bed = 0.75).
	public static final Block SAFETY_NET = register("safety_net",
			SafetyNetBlock::new,
			BlockBehaviour.Properties.of()
					.mapColor(MapColor.WOOL)
					.strength(0.4f)
					.sound(SoundType.WOOL)
					.noOcclusion()
					.ignitedByLava()
					.bounceRestitution(0.3f));

	// Wall of a mold. Fast to break (hardness 0.5 like dirt: ~0.75 s by hand, ~0.15 s with an axe),
	// but not by accident. Always drops itself, so it's reusable.
	public static final Block FORMWORK = register("formwork",
			Block::new,
			BlockBehaviour.Properties.of()
					.mapColor(MapColor.WOOD)
					.instrument(NoteBlockInstrument.BASS)
					.strength(0.5f)
					.sound(SoundType.WOOD));

	// Floats like iron bars. Breaks fast by hand too, and always drops itself.
	public static final Block REBAR = register("rebar",
			RebarBlock::new,
			BlockBehaviour.Properties.of()
					.mapColor(MapColor.METAL)
					.strength(1.0f)
					.sound(SoundType.IRON)
					.noOcclusion());

	// Like stone: hardness 1.5, blast resistance 6. Needs a pickaxe to drop.
	public static final Block CONCRETE = register("concrete",
			Block::new,
			BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_LIGHT_GRAY)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.requiresCorrectToolForDrops()
					.strength(1.5f, 6.0f)
					.sound(SoundType.STONE));

	// Mines like an iron block (hardness 5), resists explosions like obsidian (1200 = creeper- and TNT-proof).
	// Needs an iron pickaxe or better (tag #minecraft:needs_iron_tool).
	public static final Block REINFORCED_CONCRETE = register("reinforced_concrete",
			Block::new,
			BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_GRAY)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.requiresCorrectToolForDrops()
					.strength(5.0f, 1200.0f)
					.sound(SoundType.STONE));

	private ModBlocks() {
	}

	/** Registers a block and its BlockItem. Both need their own ID (setId) before they are created. */
	private static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, SiteWorks.id(name));
		T block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(properties.setId(blockKey)));

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, SiteWorks.id(name));
		// useBlockDescriptionPrefix: the item uses the block's name ("block.siteworks.safety_net").
		Registry.register(BuiltInRegistries.ITEM, itemKey,
				new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
		return block;
	}

	public static void init() {
	}
}
