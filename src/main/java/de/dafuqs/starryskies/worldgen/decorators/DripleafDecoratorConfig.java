package de.dafuqs.starryskies.worldgen.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.*;

public record DripleafDecoratorConfig(int tries) implements SphereDecoratorConfig {

	public static final Codec<DripleafDecoratorConfig> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					Codec.INT.fieldOf("tries").forGetter(config -> config.tries)
			).apply(instance, DripleafDecoratorConfig::new));

}
