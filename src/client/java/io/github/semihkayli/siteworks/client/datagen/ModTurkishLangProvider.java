package io.github.semihkayli.siteworks.client.datagen;

import io.github.semihkayli.siteworks.registry.ModBlocks;
import io.github.semihkayli.siteworks.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

/** Writes assets/siteworks/lang/tr_tr.json. */
public class ModTurkishLangProvider extends FabricLanguageProvider {
	public ModTurkishLangProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, "tr_tr", registries);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider registries, TranslationBuilder builder) {
		builder.add("itemGroup.siteworks.site_works", "Site Works");
		// "Kazma" is already the vanilla pickaxe, so the mattock uses its real name "Kazma Çapa".
		builder.add(ModItems.MATTOCK, "Kazma Çapa");
		builder.add(ModItems.SLEDGEHAMMER, "Balyoz");
		builder.add(ModItems.HARD_HAT, "Baret");
		builder.add(ModBlocks.SAFETY_NET, "Güvenlik Ağı");
		builder.add(ModBlocks.FORMWORK, "Kalıp");
		builder.add(ModBlocks.REBAR, "İnşaat Demiri");
		builder.add(ModBlocks.CONCRETE, "Beton");
		builder.add(ModBlocks.REINFORCED_CONCRETE, "Betonarme");
		builder.add(ModItems.GHOST_TEST, "Hayalet Testi (test eşyası)");
		builder.add("message.siteworks.ghost_test.spawned", "%s hayalet blok oluşturuldu");
		builder.add("message.siteworks.ghost_test.removed", "%s hayalet blok silindi");
	}
}
