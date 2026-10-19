package io.github.semihkayli.siteworks.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** Short messages in the action bar (the line above the hotbar). One place, so every tool looks the same. */
public final class Messages {
	private Messages() {
	}

	/** A red message: something didn't work ("Out of mortar!", "The formwork leaks!"). */
	public static void error(Player player, Component text) {
		player.sendOverlayMessage(text.copy().withStyle(ChatFormatting.RED));
	}
}
