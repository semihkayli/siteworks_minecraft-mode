package io.github.semihkayli.siteworks.client.datagen;

import io.github.semihkayli.siteworks.registry.ModBlocks;
import io.github.semihkayli.siteworks.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

/** Writes assets/siteworks/lang/en_us.json. */
public class ModEnglishLangProvider extends FabricLanguageProvider {
	public ModEnglishLangProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, "en_us", registries);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider registries, TranslationBuilder builder) {
		builder.add("itemGroup.siteworks.site_works", "Site Works");
		builder.add(ModItems.MATTOCK, "Mattock");
		builder.add(ModItems.SLEDGEHAMMER, "Sledgehammer");
		builder.add(ModItems.HARD_HAT, "Hard Hat");
		builder.add(ModBlocks.SAFETY_NET, "Safety Net");
		builder.add(ModItems.CEMENT_MIX, "Cement Mix");
		builder.add(ModItems.CEMENT, "Cement");
		builder.add(ModItems.CONCRETE_BUCKET, "Concrete Bucket");
		builder.add("item.siteworks.concrete_bucket.amount", "Concrete: %s/%s blocks");
		builder.add("message.siteworks.pour.leaks", "The formwork leaks!");
		builder.add("message.siteworks.pour.full", "The mold is full.");
		builder.add(ModBlocks.FORMWORK, "Formwork");
		builder.add(ModBlocks.REBAR, "Rebar");
		builder.add(ModBlocks.CONCRETE, "Concrete");
		builder.add(ModBlocks.REINFORCED_CONCRETE, "Reinforced Concrete");
		builder.add(ModBlocks.WET_CONCRETE, "Wet Concrete");
		builder.add(ModItems.GHOST_TEST, "Ghost Test (test item)");
		builder.add("message.siteworks.ghost_test.spawned", "Spawned %s ghost blocks");
		builder.add("message.siteworks.ghost_test.removed", "Removed %s ghost blocks");
	}
}
