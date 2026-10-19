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
		builder.add(ModItems.CEMENT_MIX, "Çimento Karışımı");
		builder.add(ModItems.CEMENT, "Çimento");
		builder.add(ModBlocks.CONCRETE_MIXER, "Betoniyer");
		builder.add("message.siteworks.mixer.status", "Çimento %s · Kum %s · Çakıl %s · Su %s/%s · Hazır %s/%s");
		builder.add("message.siteworks.mixer.mixing", " · Karıştırıyor...");
		builder.add("message.siteworks.mixer.no_concrete", "Henüz hazır beton yok.");
		builder.add("message.siteworks.mixer.slot_taken", "Betoniyerde zaten %s var. Önce onu kullan.");
		builder.add(ModItems.CONCRETE_BUCKET, "Beton Kovası");
		builder.add("item.siteworks.concrete_bucket.amount", "Beton: %s/%s blok");
		builder.add("message.siteworks.pour.leaks", "Kalıp sızdırıyor!");
		builder.add("message.siteworks.pour.full", "Kalıp dolu.");
		builder.add(ModBlocks.FORMWORK, "Kalıp");
		builder.add(ModBlocks.REBAR, "İnşaat Demiri");
		builder.add(ModBlocks.CONCRETE, "Beton");
		builder.add(ModBlocks.REINFORCED_CONCRETE, "Betonarme");
		builder.add(ModBlocks.WET_CONCRETE, "Taze Beton");
		builder.add(ModItems.MORTAR, "Harç");
		builder.add(ModItems.TROWEL, "Mala");
		builder.add("item.siteworks.trowel.charge", "Harç: %s/%s blok");
		builder.add("item.siteworks.trowel.hint", "Eğil + havaya sağ tık: sıra başlangıcını iptal et");
		builder.add("item.siteworks.trowel.row_hint", "Sıra: önce sıranın başlayacağı, sonra biteceği yere tıkla");
		builder.add("item.siteworks.trowel.row_start_line", "Sıra başlangıcı: %s %s %s");
		builder.add("message.siteworks.trowel.row_start", "Sıra başlangıcı seçildi. Şimdi sıranın biteceği yere tıkla (en fazla %s).");
		builder.add("message.siteworks.trowel.row_cancel", "Sıra başlangıcı kaldırıldı.");
		builder.add("message.siteworks.trowel.not_straight", "Düz bir çizgi değil. Yeni sıra başlangıcı burası.");
		builder.add("message.siteworks.trowel.too_long", "Çok uzun (en fazla %s)! Yeni sıra başlangıcı burası.");
		builder.add("message.siteworks.trowel.need_block", "Tuğlaları diğer elinde tut ya da bir örgü bloğuna tıkla.");
		builder.add("message.siteworks.trowel.no_mortar", "Harç bitti!");
		builder.add("message.siteworks.trowel.no_blocks", "%s bitti!");
		builder.add("message.siteworks.trowel.blocked", "Önünde bir engel var.");
		builder.add(ModItems.STEEL_INGOT, "Çelik Külçe");
		builder.add(ModBlocks.STEEL_BEAM, "Çelik Kiriş");
		builder.add("message.siteworks.beam.blocked", "Önünde bir engel var.");
		builder.add("message.siteworks.beam.too_far", "Çok uzak! Kirişin ucuna yaklaş.");
		builder.add(ModItems.GHOST_TEST, "Hayalet Testi (test eşyası)");
		builder.add("message.siteworks.ghost_test.spawned", "%s hayalet blok oluşturuldu");
		builder.add("message.siteworks.ghost_test.removed", "%s hayalet blok silindi");
	}
}
