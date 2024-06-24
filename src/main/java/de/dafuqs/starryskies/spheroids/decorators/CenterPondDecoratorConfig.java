package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import net.minecraft.block.*;
import net.minecraft.loot.*;
import net.minecraft.registry.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public record CenterPondDecoratorConfig(BlockState beachState, BlockState fluidState, RegistryKey<LootTable> lootTable, float lootTableChance) implements SpheroidFeatureConfig {
	
	public static final Codec<CenterPondDecoratorConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("beach_block").forGetter(decorator -> decorator.beachState),
					BLOCKSTATE_STRING_CODEC.fieldOf("fluid_block").forGetter(decorator -> decorator.fluidState),
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).fieldOf("loot_table").forGetter(decorator -> decorator.lootTable),
					Codec.FLOAT.fieldOf("loot_table_chance").forGetter(decorator -> decorator.lootTableChance)
			).apply(instance, CenterPondDecoratorConfig::new)
	);
	
}
