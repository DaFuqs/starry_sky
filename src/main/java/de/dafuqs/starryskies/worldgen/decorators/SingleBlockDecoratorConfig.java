package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;

public record SingleBlockDecoratorConfig(BlockState state, float chance) implements SphereDecoratorConfig {

	public static final Codec<SingleBlockDecoratorConfig> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					BlockState.CODEC.fieldOf("block").forGetter(decorator -> decorator.state),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(decorator -> decorator.chance)
			).apply(instance, SingleBlockDecoratorConfig::new));

}
