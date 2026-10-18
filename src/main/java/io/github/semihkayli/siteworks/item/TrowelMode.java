package io.github.semihkayli.siteworks.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

/** The Trowel's modes. Sneak + right-click the air switches to the next one. */
public enum TrowelMode implements StringRepresentable {
	/** Up to 8 blocks in a straight line out from the face you click. */
	ROW("row", 8),
	/** Up to 16 blocks: one new layer on the whole wall face (Phase 3 step 4). */
	COURSE("course", 16);

	// Saved in the world as "row" / "course", sent to clients as a small number.
	public static final Codec<TrowelMode> CODEC = StringRepresentable.fromEnum(TrowelMode::values);
	public static final StreamCodec<ByteBuf, TrowelMode> STREAM_CODEC = ByteBufCodecs.idMapper(
			ByIdMap.continuous(TrowelMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO), TrowelMode::ordinal);

	private final String name;
	private final int maxBlocks;

	TrowelMode(String name, int maxBlocks) {
		this.name = name;
		this.maxBlocks = maxBlocks;
	}

	public int maxBlocks() {
		return maxBlocks;
	}

	public TrowelMode next() {
		return values()[(ordinal() + 1) % values().length];
	}

	/** "Row (8)" / "Sıra (8)". */
	public Component displayName() {
		return Component.translatable("item.siteworks.trowel.mode." + name);
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
