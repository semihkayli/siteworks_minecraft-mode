package io.github.semihkayli.siteworks;

import io.github.semihkayli.siteworks.event.AreaMining;
import io.github.semihkayli.siteworks.event.HardHatProtection;
import io.github.semihkayli.siteworks.registry.ModBlockEntities;
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

	/** Writes to the console and run/logs/latest.log, with our mod ID as the name. */
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Runs once when the game starts, on both the client and the server. Registers all our content. */
	@Override
	public void onInitialize() {
		ModDataComponents.init();
		ModBlocks.init();
		ModBlockEntities.init();
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
