package io.github.semihkayli.siteworks.client.datagen;

import io.github.semihkayli.siteworks.registry.ModBlocks;
import io.github.semihkayli.siteworks.registry.ModItems;
import java.util.List;
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
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
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

				// Clay ball + bone meal -> 2 Cement Mix. Calcite (like real limestone) works instead of bone meal.
				shapeless(RecipeCategory.MISC, ModItems.CEMENT_MIX, 2)
						.requires(Items.CLAY_BALL)
						.requires(Items.BONE_MEAL)
						.unlockedBy("has_clay_ball", has(Items.CLAY_BALL))
						.save(output);
				// A 2nd recipe for the same item needs its own name. Fabric adds our mod ID ("siteworks:") to it.
				shapeless(RecipeCategory.MISC, ModItems.CEMENT_MIX, 2)
						.requires(Items.CLAY_BALL)
						.requires(Items.CALCITE)
						.unlockedBy("has_clay_ball", has(Items.CLAY_BALL))
						.save(output, "cement_mix_from_calcite");

				// Smelt Cement Mix -> Cement: furnace 10 s (like ores), blast furnace 5 s. 0.1 XP each.
				oreSmelting(List.of(ModItems.CEMENT_MIX), RecipeCategory.MISC, CookingBookCategory.MISC,
						ModItems.CEMENT, 0.1f, 200, "cement");
				oreBlasting(List.of(ModItems.CEMENT_MIX), RecipeCategory.MISC, CookingBookCategory.MISC,
						ModItems.CEMENT, 0.1f, 100, "cement");

				// A cauldron as the drum, iron for the frame, redstone as the "motor".
				shaped(RecipeCategory.MISC, ModBlocks.CONCRETE_MIXER)
						.define('R', Items.REDSTONE)
						.define('C', Items.CAULDRON)
						.define('I', Items.IRON_INGOT)
						.pattern(" R ")
						.pattern("ICI")
						.pattern("I I")
						.unlockedBy("has_cauldron", has(Items.CAULDRON))
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

				// 1 Cement + 4 Sand (sand or red sand) -> 8 Mortar (the mixer's ratio). 1 Mortar = 8 blocks with the Trowel.
				shapeless(RecipeCategory.MISC, ModItems.MORTAR, 8)
						.requires(ModItems.CEMENT)
						// Not the vanilla #sand tag: it has suspicious sand too. The same sand as the mixer.
						.requires(Ingredient.of(Items.SAND, Items.RED_SAND), 4)
						.unlockedBy("has_cement", has(ModItems.CEMENT))
						.save(output);

				// Smelt an Iron Ingot again -> Steel Ingot (the coal fuel = "iron + coal"). Furnace 10 s, blast furnace 5 s.
				oreSmelting(List.of(Items.IRON_INGOT), RecipeCategory.MISC, CookingBookCategory.MISC,
						ModItems.STEEL_INGOT, 0.1f, 200, "steel_ingot");
				oreBlasting(List.of(Items.IRON_INGOT), RecipeCategory.MISC, CookingBookCategory.MISC,
						ModItems.STEEL_INGOT, 0.1f, 100, "steel_ingot");

				// An "I" shape (7 steel) -> 16 beams (about the iron per piece of vanilla iron bars).
				// One craft = two 8-long bridges. Was 8, the user wanted it more generous (Phase 3 step 2b).
				shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_BEAM, 16)
						.define('S', ModItems.STEEL_INGOT)
						.pattern("SSS")
						.pattern(" S ")
						.pattern("SSS")
						.unlockedBy("has_steel_ingot", has(ModItems.STEEL_INGOT))
						.save(output);
			}
		};
	}
}
