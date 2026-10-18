package io.github.semihkayli.siteworks;

import io.github.semihkayli.siteworks.event.AreaMining;
import io.github.semihkayli.siteworks.event.HardHatProtection;
import io.github.semihkayli.siteworks.registry.ModBlocks;
import io.github.semihkayli.siteworks.registry.ModCreativeTabs;
import io.github.semihkayli.siteworks.registry.ModDataComponents;
import io.github.semihkayli.siteworks.registry.ModItems;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteWorks implements ModInitializer {
	public static final String MOD_ID = "siteworks";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModDataComponents.init();
		ModBlocks.init();
		ModItems.init();
		ModCreativeTabs.init();
		AreaMining.init();
		HardHatProtection.init();

		LOGGER.info("Site Works loaded. Time to build!");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
