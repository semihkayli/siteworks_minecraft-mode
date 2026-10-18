package io.github.semihkayli.siteworks.client.datagen;

import io.github.semihkayli.siteworks.registry.ModBlocks;
import io.github.semihkayli.siteworks.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Writes data/siteworks/recipe/*.json. Each recipe also gets a small "recipe advancement",
 * which unlocks it in the recipe book when the player picks up the ingredient (unlockedBy).
 */
public class ModRecipeProvider extends FabricRecipeProvider {
	public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipes,
			BootstrapContext<Advancement> advancements) {
		return new RecipeProvider(recipes, advancements) {
			@Override
			public void buildRecipes() {
				shaped(RecipeCategory.TOOLS, ModItems.MATTOCK)
						.define('X', ItemTags.IRON_TOOL_MATERIALS)
						.define('#', Items.STICK)
						.pattern("XXX")
						.pattern("X# ")
						.pattern(" # ")
						.unlockedBy("has_iron_ingot", has(ItemTags.IRON_TOOL_MATERIALS))
						.save(output);

				shaped(RecipeCategory.TOOLS, ModItems.SLEDGEHAMMER)
						.define('X', ItemTags.IRON_TOOL_MATERIALS)
						.define('#', Items.STICK)
						.pattern("XXX")
						.pattern("XXX")
						.pattern(" # ")
						.unlockedBy("has_iron_ingot", has(ItemTags.IRON_TOOL_MATERIALS))
						.save(output);

				// Like the iron helmet, with yellow dye in the hole.
				shaped(RecipeCategory.COMBAT, ModItems.HARD_HAT)
						.define('X', Items.IRON_INGOT)
						.define('Y', Items.DYE.yellow())
						.pattern("XXX")
						.pattern("XYX")
						.unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
						.save(output);

				// 5 string in an X shape -> 4 nets.
				shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SAFETY_NET, 4)
						.define('S', Items.STRING)
						.pattern("S S")
						.pattern(" S ")
						.pattern("S S")
						.unlockedBy("has_string", has(Items.STRING))
						.save(output);

				// 2 planks (any wood) + 2 sticks, diagonal -> 4 formwork. The mirrored shape works too.
				shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.FORMWORK, 4)
						.define('P', ItemTags.PLANKS)
						.define('S', Items.STICK)
						.pattern("PS")
						.pattern("SP")
						.unlockedBy("has_planks", has(ItemTags.PLANKS))
						.save(output);

				// 3 iron ingots in a column -> 8 rebar (the same iron per piece as vanilla iron bars).
				shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.REBAR, 8)
						.define('I', Items.IRON_INGOT)
						.pattern("I")
						.pattern("I")
						.pattern("I")
						.unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
						.save(output);
			}
		};
	}
}
