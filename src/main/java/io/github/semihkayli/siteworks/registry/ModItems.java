package io.github.semihkayli.siteworks.registry;

import io.github.semihkayli.siteworks.SiteWorks;
import io.github.semihkayli.siteworks.item.AreaToolItem;
import io.github.semihkayli.siteworks.item.ConcreteBucketItem;
import io.github.semihkayli.siteworks.item.GhostTestItem;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

/** All items of the mod. Each item is registered once, when the game starts. */
public final class ModItems {
	// Iron shovel stats (damage 1.5, speed -3.0), but 500 durability (2x iron).
	public static final Item MATTOCK = register("mattock",
			properties -> new AreaToolItem(ModTags.SOFT_GROUND, properties),
			new Item.Properties().shovel(ToolMaterial.IRON, 1.5f, -3.0f).durability(500));

	// Iron pickaxe tier (mines what an iron pickaxe mines), heavy hit (damage 4, slow like an axe), 500 durability.
	public static final Item SLEDGEHAMMER = register("sledgehammer",
			properties -> new AreaToolItem(ModTags.SLEDGE_BREAKABLE, properties),
			new Item.Properties().pickaxe(ToolMaterial.IRON, 4.0f, -3.2f).durability(500));

	// Iron helmet stats (armor 2, durability 165, repair with iron), but the yellow golden helmet look.
	private static final ArmorMaterial HARD_HAT_MATERIAL = new ArmorMaterial(
			ArmorMaterials.IRON.durability(),
			ArmorMaterials.IRON.defense(),
			ArmorMaterials.IRON.enchantmentValue(),
			ArmorMaterials.IRON.equipSound(),
			ArmorMaterials.IRON.toughness(),
			ArmorMaterials.IRON.knockbackResistance(),
			ArmorMaterials.IRON.repairIngredient(),
			EquipmentAssets.GOLD);

	public static final Item HARD_HAT = register("hard_hat",
			Item::new,
			new Item.Properties().humanoidArmor(HARD_HAT_MATERIAL, ArmorType.HELMET));

	// Concrete chain: clay ball + bone meal (or calcite) -> Cement Mix -> smelt -> Cement -> Concrete Mixer.
	public static final Item CEMENT_MIX = register("cement_mix", Item::new, new Item.Properties());
	public static final Item CEMENT = register("cement", Item::new, new Item.Properties());

	// Filled at the Concrete Mixer from a vanilla empty bucket. Pours up to 16 blocks, gives the empty bucket back.
	public static final Item CONCRETE_BUCKET = register("concrete_bucket",
			ConcreteBucketItem::new,
			new Item.Properties().stacksTo(1).component(ModDataComponents.CONCRETE_AMOUNT, ConcreteBucketItem.CAPACITY));

	// TEST ITEM (Phase 1 ghost test). Creative only, no recipe. Replaced by the Survey Stake in Phase 5.
	public static final Item GHOST_TEST = register("ghost_test",
			GhostTestItem::new,
			new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

	private ModItems() {
	}

	/**
	 * Registers one item. Since 1.21.2 the item must know its own ID (setId)
	 * before it is created, so we build the key first.
	 */
	private static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, SiteWorks.id(name));
		T item = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	/** Called from SiteWorks.onInitialize(). It makes Java load this class, which registers the items above. */
	public static void init() {
	}
}
