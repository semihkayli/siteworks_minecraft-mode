package io.github.semihkayli.siteworks.registry;

import io.github.semihkayli.siteworks.SiteWorks;
import io.github.semihkayli.siteworks.block.entity.ConcreteMixerBlockEntity;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

/** Block entity types: which blocks have extra data + logic, and how to create it. */
public final class ModBlockEntities {
	public static final BlockEntityType<ConcreteMixerBlockEntity> CONCRETE_MIXER = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE,
			SiteWorks.id("concrete_mixer"),
			new BlockEntityType<>(ConcreteMixerBlockEntity::new, Set.of(ModBlocks.CONCRETE_MIXER)));

	private ModBlockEntities() {
	}

	public static void init() {
	}
}
