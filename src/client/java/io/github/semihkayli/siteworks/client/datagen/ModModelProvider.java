package io.github.semihkayli.siteworks.client.datagen;

import io.github.semihkayli.siteworks.SiteWorks;
import io.github.semihkayli.siteworks.registry.ModBlocks;
import io.github.semihkayli.siteworks.registry.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/** Writes assets/siteworks/items/*.json and assets/siteworks/models/**. */
public class ModModelProvider extends FabricModelProvider {
	public ModModelProvider(FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generators) {
		// Safety Net: vanilla's "trapdoor top" shape (a 3-pixel slab at the top) with the cobweb texture.
		// The cobweb texture has holes, and 26.x picks the right (cutout) render layer from the texture by itself.
		TextureMapping netTexture = TextureMapping.defaultTexture(new Material(Identifier.withDefaultNamespace("block/cobweb")));
		Identifier netModel = ModelTemplates.TRAPDOOR_TOP.create(ModBlocks.SAFETY_NET, netTexture, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.SAFETY_NET, BlockModelGenerators.plainVariant(netModel)));

		// Full cubes with one vanilla texture on all 6 sides. Their item models are made automatically.
		cubeBlock(generators, ModBlocks.FORMWORK, "block/spruce_planks");
		cubeBlock(generators, ModBlocks.CONCRETE, "block/light_gray_concrete");
		cubeBlock(generators, ModBlocks.REINFORCED_CONCRETE, "block/gray_concrete");
		// Both states (with and without rebar) look the same.
		cubeBlock(generators, ModBlocks.WET_CONCRETE, "block/gray_concrete_powder");

		// Rebar: the model (4 bars + 2 rings) is written by hand in src/main/resources/assets/siteworks/models/block/rebar.json,
		// because datagen can't draw custom shapes. Here we only point the blockstate to it.
		generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.REBAR,
				BlockModelGenerators.plainVariant(SiteWorks.id("block/rebar"))));
	}

	/** A full cube block that uses a vanilla texture on all sides. */
	private static void cubeBlock(BlockModelGenerators generators, Block block, String vanillaTexture) {
		TextureMapping texture = TextureMapping.cube(new Material(Identifier.withDefaultNamespace(vanillaTexture)));
		Identifier model = ModelTemplates.CUBE_ALL.create(block, texture, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
	}

	@Override
	public void generateItemModels(ItemModelGenerators generators) {
		// Vanilla textures as placeholders. New looks come in Phase 6.
		handheldTool(generators, ModItems.MATTOCK, "item/iron_hoe");
		handheldTool(generators, ModItems.SLEDGEHAMMER, "item/mace");
		flatItem(generators, ModItems.HARD_HAT, "item/golden_helmet");
		// A flat cobweb icon looks better in the inventory than the thin 3D block.
		flatItem(generators, ModBlocks.SAFETY_NET.asItem(), "block/cobweb");
		flatItem(generators, ModItems.GHOST_TEST, "item/amethyst_shard");
		// White raw mix -> gray cement after the furnace.
		flatItem(generators, ModItems.CEMENT_MIX, "item/sugar");
		flatItem(generators, ModItems.CEMENT, "item/gunpowder");
		flatItem(generators, ModItems.CONCRETE_BUCKET, "item/powder_snow_bucket");
	}

	/** A normal flat item (like armor or ingots), using a vanilla texture. */
	private static void flatItem(ItemModelGenerators generators, Item item, String vanillaTexture) {
		TextureMapping texture = TextureMapping.layer0(new Material(Identifier.withDefaultNamespace(vanillaTexture)));
		Identifier model = ModelTemplates.FLAT_ITEM.create(item, texture, generators.modelOutput);
		generators.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
	}

	/** A tool held like a pickaxe, using a vanilla texture. */
	private static void handheldTool(ItemModelGenerators generators, Item item, String vanillaTexture) {
		TextureMapping texture = TextureMapping.layer0(new Material(Identifier.withDefaultNamespace(vanillaTexture)));
		Identifier model = ModelTemplates.FLAT_HANDHELD_ITEM.create(item, texture, generators.modelOutput);
		generators.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
	}
}
