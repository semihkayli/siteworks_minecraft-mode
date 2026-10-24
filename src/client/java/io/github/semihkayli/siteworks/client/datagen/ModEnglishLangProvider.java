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
		builder.add(ModBlocks.CONCRETE_MIXER, "Concrete Mixer");
		builder.add("message.siteworks.mixer.status", "Cement %s · Sand %s · Gravel %s · Water %s/%s · Ready %s/%s");
		builder.add("message.siteworks.mixer.mixing", " · Mixing...");
		builder.add("message.siteworks.mixer.no_concrete", "No concrete ready yet.");
		builder.add("message.siteworks.mixer.slot_taken", "The mixer already holds %s. Use it up first.");
		builder.add(ModItems.CONCRETE_BUCKET, "Concrete Bucket");
		builder.add("item.siteworks.concrete_bucket.amount", "Concrete: %s/%s blocks");
		builder.add("message.siteworks.pour.leaks", "The formwork leaks!");
		builder.add("message.siteworks.pour.full", "The mold is full.");
		builder.add("message.siteworks.pour.protected", "You can't pour concrete here (protected area or the edge of the world).");
		builder.add("message.siteworks.pour.no_formwork", "Wet concrete needs formwork here! (red dots)");
		builder.add(ModBlocks.FORMWORK, "Formwork");
		builder.add(ModBlocks.REBAR, "Rebar");
		builder.add(ModBlocks.CONCRETE, "Concrete");
		builder.add(ModBlocks.REINFORCED_CONCRETE, "Reinforced Concrete");
		builder.add("message.siteworks.foundation.ok", "Foundation: %s layer(s) → buildings up to %s blocks high (up to y %s)");
		builder.add("message.siteworks.foundation.none", "Not a foundation: nothing solid under it (air, water...).");
		builder.add("message.siteworks.foundation.too_tall", "Foundation too thin for this height! Add a layer (max y %s).");
		builder.add(ModBlocks.WET_CONCRETE, "Wet Concrete");
		builder.add(ModItems.MORTAR, "Mortar");
		builder.add(ModItems.TROWEL, "Trowel");
		builder.add("item.siteworks.trowel.charge", "Mortar: %s/%s blocks");
		builder.add("item.siteworks.trowel.hint", "Sneak + right-click the air: cancel the row start");
		builder.add("item.siteworks.trowel.row_hint", "Row: click where the row starts, then where it ends");
		builder.add("item.siteworks.trowel.row_start_line", "Row start: %s %s %s");
		builder.add("message.siteworks.trowel.row_start", "Row start set. Now click where the row ends (max %s).");
		builder.add("message.siteworks.trowel.row_cancel", "Row start removed.");
		builder.add("message.siteworks.trowel.not_straight", "Not a straight line. New row start set here.");
		builder.add("message.siteworks.trowel.too_long", "Too long (max %s)! New row start set here.");
		builder.add("message.siteworks.trowel.need_block", "Hold the bricks in your other hand, or click a masonry block.");
		builder.add("message.siteworks.trowel.no_mortar", "Out of mortar!");
		builder.add("message.siteworks.trowel.no_blocks", "No more %s!");
		builder.add("message.siteworks.trowel.blocked", "Something is in the way.");
		builder.add(ModItems.STEEL_INGOT, "Steel Ingot");
		builder.add(ModBlocks.STEEL_BEAM, "Steel Beam");
		builder.add("message.siteworks.beam.blocked", "Something is in the way.");
		builder.add("message.siteworks.beam.too_far", "Too far! Walk closer to the end of the beam.");
		builder.add(ModItems.GHOST_TEST, "Ghost Test (test item)");
		builder.add("message.siteworks.ghost_test.spawned", "Spawned %s ghost blocks");
		builder.add("message.siteworks.ghost_test.removed", "Removed %s ghost blocks");
	}
}
