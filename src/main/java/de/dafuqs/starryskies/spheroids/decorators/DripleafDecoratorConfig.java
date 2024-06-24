package de.dafuqs.starryskies.spheroids.decorators;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.spheroids.decoration.*;

public record DripleafDecoratorConfig(int tries) implements SpheroidFeatureConfig {
	
	public static final Codec<DripleafDecoratorConfig> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					Codec.INT.fieldOf("tries").forGetter(config -> config.tries)
			).apply(instance, DripleafDecoratorConfig::new));
	
}
