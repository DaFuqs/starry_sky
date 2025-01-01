package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;

public record BambooDecoratorConfig(float chance, float saplingChance, BlockState bambooBlockState,
									BlockState bambooSaplingBlockState) implements SphereDecoratorConfig {

	public static final Codec<BambooDecoratorConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.FLOAT.fieldOf("chance").forGetter(decorator -> decorator.chance),
					Codec.FLOAT.fieldOf("sapling_chance").forGetter(decorator -> decorator.saplingChance),
					BlockState.CODEC.fieldOf("bamboo_block").forGetter(decorator -> decorator.bambooBlockState),
					BlockState.CODEC.fieldOf("sapling_block").forGetter(decorator -> decorator.bambooSaplingBlockState)
			).apply(instance, BambooDecoratorConfig::new)
	);

}