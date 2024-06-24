package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import net.minecraft.block.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public record HangingCaveBlockDecoratorConfig(BlockState block, float chance) implements SpheroidFeatureConfig {
	
	public static final Codec<HangingCaveBlockDecoratorConfig> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("block").forGetter(decorator -> decorator.block),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(decorator -> decorator.chance)
			).apply(instance, HangingCaveBlockDecoratorConfig::new));
	
}
