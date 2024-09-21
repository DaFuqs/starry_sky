package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.*;
import net.minecraft.block.*;

import static de.dafuqs.starryskies.Support.*;

public record CaveBottomDecoratorConfig(BlockState state, float chance) implements SphereDecoratorConfig {

	public static final Codec<CaveBottomDecoratorConfig> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					BLOCKSTATE_STRING_CODEC.fieldOf("block").forGetter(decorator -> decorator.state),
					Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(decorator -> decorator.chance)
			).apply(instance, CaveBottomDecoratorConfig::new));

}
