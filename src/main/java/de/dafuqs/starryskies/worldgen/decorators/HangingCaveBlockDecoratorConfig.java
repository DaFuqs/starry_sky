package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;

public record HangingCaveBlockDecoratorConfig(BlockState block, float chance) implements SphereDecoratorConfig {

	public static final Codec<HangingCaveBlockDecoratorConfig> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					BlockState.CODEC.fieldOf("block").forGetter(decorator -> decorator.block),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(decorator -> decorator.chance)
			).apply(instance, HangingCaveBlockDecoratorConfig::new));

}
