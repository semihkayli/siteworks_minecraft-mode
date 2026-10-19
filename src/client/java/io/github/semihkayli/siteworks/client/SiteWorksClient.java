package io.github.semihkayli.siteworks.client;

import net.fabricmc.api.ClientModInitializer;

/** Client-only setup (runs only on the player's computer, never on a dedicated server). */
public class SiteWorksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TrowelPreview.register();
		PourPreview.register();
	}
}
