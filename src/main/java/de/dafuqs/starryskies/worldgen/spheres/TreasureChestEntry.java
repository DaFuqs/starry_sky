package de.dafuqs.starryskies.worldgen.spheres;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;
import net.minecraft.util.dynamic.*;

public record TreasureChestEntry(RegistryKey<LootTable> lootTable, Float chance) {
	public static final Codec<TreasureChestEntry> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter((t) -> t.lootTable),
			Codecs.POSITIVE_FLOAT.fieldOf("chance").forGetter((t) -> t.chance)
	).apply(instance, TreasureChestEntry::new));
}