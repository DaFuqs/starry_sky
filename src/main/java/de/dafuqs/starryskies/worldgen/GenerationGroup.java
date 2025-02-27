package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import net.minecraft.util.*;

public record GenerationGroup(Identifier systemGeneratorId, float weight) {

	public static final Codec<GenerationGroup> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Identifier.CODEC.fieldOf("system_generator").forGetter(generator -> generator.systemGeneratorId),
					Codec.FLOAT.fieldOf("weight").forGetter(generator -> generator.weight)
			).apply(instance, GenerationGroup::new)
	);
	
}
