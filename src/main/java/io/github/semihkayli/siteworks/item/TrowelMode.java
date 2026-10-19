package io.github.semihkayli.siteworks.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

/**
 * The Trowel's modes. Only Row for now: Course mode was dropped by the user (Phase 3 step 4).
 * Phase 5 adds "Blueprint fill", and then sneak + right-click the air can switch modes again.
 */
public enum TrowelMode implements StringRepresentable {
	/** "From -> to": click where the row starts, then where it ends (both included). Up to 8 blocks in a straight line. */
	ROW("row", 8);

	// Saved in the world as "row", sent to clients as a small number.
	// An unknown saved name (e.g. the old "course") loads as ROW, so old trowels don't break.
	public static final Codec<TrowelMode> CODEC = Codec.STRING.xmap(TrowelMode::byName, TrowelMode::getSerializedName);
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

	private static TrowelMode byName(String name) {
		for (TrowelMode mode : values()) {
			if (mode.name.equals(name)) {
				return mode;
			}
		}
		return ROW;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
