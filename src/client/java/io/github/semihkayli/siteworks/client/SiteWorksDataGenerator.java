package io.github.semihkayli.siteworks.client;

import io.github.semihkayli.siteworks.client.datagen.ModBlockLootProvider;
import io.github.semihkayli.siteworks.client.datagen.ModBlockTagProvider;
import io.github.semihkayli.siteworks.client.datagen.ModEnglishLangProvider;
import io.github.semihkayli.siteworks.client.datagen.ModItemTagProvider;
import io.github.semihkayli.siteworks.client.datagen.ModModelProvider;
import io.github.semihkayli.siteworks.client.datagen.ModRecipeProvider;
import io.github.semihkayli.siteworks.client.datagen.ModTurkishLangProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Runs with {@code gradlew runDatagen}. Each provider writes one kind of JSON
 * into src/main/generated/. Never edit those files by hand: change the provider and run datagen again.
 */
public class SiteWorksDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModEnglishLangProvider::new);
		pack.addProvider(ModTurkishLangProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModBlockLootProvider::new);
		pack.addProvider(ModItemTagProvider::new);
	}
}
