package io.github.semihkayli.siteworks.registry;

import io.github.semihkayli.siteworks.SiteWorks;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/** Our own tab in the creative inventory. */
public final class ModCreativeTabs {
	public static final CreativeModeTab SITE_WORKS = Registry.register(
			BuiltInRegistries.CREATIVE_MODE_TAB,
			SiteWorks.id("site_works"),
			FabricCreativeModeTab.builder()
					.title(Component.translatable("itemGroup.siteworks.site_works"))
					.icon(() -> new ItemStack(ModItems.HARD_HAT))
					.displayItems((parameters, output) -> {
						output.accept(ModItems.HARD_HAT);
						output.accept(ModItems.MATTOCK);
						output.accept(ModItems.SLEDGEHAMMER);
						output.accept(ModBlocks.SAFETY_NET);
						output.accept(ModItems.CEMENT_MIX);
						output.accept(ModItems.CEMENT);
						output.accept(ModBlocks.FORMWORK);
						output.accept(ModBlocks.REBAR);
						output.accept(ModBlocks.CONCRETE);
						output.accept(ModBlocks.REINFORCED_CONCRETE);
						output.accept(ModItems.GHOST_TEST);
					})
					.build());

	private ModCreativeTabs() {
	}

	public static void init() {
	}
}
