package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import net.minecraft.block.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public record StackedBlockDecoratorConfig(BlockState block, float chance, int minHeight, int maxHeight) implements SpheroidFeatureConfig {
	
	public static final Codec<StackedBlockDecoratorConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("block").forGetter(decorator -> decorator.block),
					Codec.FLOAT.fieldOf("chance").forGetter(decorator -> decorator.chance),
					Codec.INT.fieldOf("min_height").forGetter(decorator -> decorator.minHeight),
					Codec.INT.fieldOf("max_height").forGetter(decorator -> decorator.maxHeight)
			).apply(instance, StackedBlockDecoratorConfig::new)
	);
	
}
