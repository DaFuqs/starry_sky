package de.dafuqs.starryskies.spheroids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.*;
import net.minecraft.registry.Registries;

public class SpheroidEntitySpawnDefinition {

	public static final Codec<SpheroidEntitySpawnDefinition> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Registries.ENTITY_TYPE.getCodec().fieldOf("type").forGetter(def -> def.entityType),
					Codec.INT.fieldOf("min_count").forGetter(def -> def.minCount),
					Codec.INT.fieldOf("max_count").forGetter(def -> def.maxCount),
					Codec.FLOAT.fieldOf("chance").forGetter(def -> def.chance)
			).apply(instance, SpheroidEntitySpawnDefinition::new)
	);
	
	public EntityType<?> entityType;
	public float chance;
	public int minCount;
	public int maxCount;
	
	public SpheroidEntitySpawnDefinition(EntityType<?> entityType, int minCount, int maxCount, float chance) {
		this.entityType = entityType;
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.chance = chance;
	}
	
}
