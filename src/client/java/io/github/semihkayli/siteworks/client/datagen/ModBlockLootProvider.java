package io.github.semihkayli.siteworks.client.datagen;

import io.github.semihkayli.siteworks.block.WetConcreteBlock;
import io.github.semihkayli.siteworks.registry.ModBlocks;
import io.github.semihkayli.siteworks.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

/** Writes data/siteworks/loot_table/blocks/*.json (what a block drops when broken). */
public class ModBlockLootProvider extends FabricBlockLootSubProvider {
	public ModBlockLootProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public void generate() {
		dropSelf(ModBlocks.SAFETY_NET);
		dropSelf(ModBlocks.FORMWORK);
		dropSelf(ModBlocks.REBAR);
		dropSelf(ModBlocks.CONCRETE_MIXER);

		// Recycling: broken with the Sledgehammer (and no Silk Touch) -> gravel. Otherwise -> the block itself.
		add(ModBlocks.CONCRETE, block -> LootTable.lootTable()
				.withPool(selfOrGravel(block)));
		// The same, plus the rebar comes back.
		add(ModBlocks.REINFORCED_CONCRETE, block -> LootTable.lootTable()
				.withPool(selfOrGravel(block))
				.withPool(LootPool.lootPool()
						.setRolls(ContextIntProviders.exactly(1))
						.add(LootItem.lootTableItem(ModBlocks.REBAR))
						.when(sledgehammerWithoutSilkTouch())));

		// Wet concrete drops nothing. Only the rebar comes back, if the block has reinforced=true.
		add(ModBlocks.WET_CONCRETE, block -> LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.setRolls(ContextIntProviders.exactly(1))
						.add(LootItem.lootTableItem(ModBlocks.REBAR))
						.when(MatchBlock.blockMatches(blocks, block,
								StatePropertiesPredicate.Builder.properties().hasProperty(WetConcreteBlock.REINFORCED, true)))));
	}

	/** One pool: gravel when broken with the Sledgehammer, otherwise the block itself. */
	private LootPool.Builder selfOrGravel(Block block) {
		return LootPool.lootPool()
				.setRolls(ContextIntProviders.exactly(1))
				.add(LootItem.lootTableItem(Items.GRAVEL)
						.when(sledgehammerWithoutSilkTouch())
						.otherwise(applyExplosionCondition(block, LootItem.lootTableItem(block))));
	}

	/** "The tool is our Sledgehammer, and it has no Silk Touch." */
	private LootItemCondition.Builder sledgehammerWithoutSilkTouch() {
		return MatchTool.toolMatches(ItemPredicate.Builder.item().of(items, ModItems.SLEDGEHAMMER))
				.and(doesNotHaveSilkTouch());
	}
}
