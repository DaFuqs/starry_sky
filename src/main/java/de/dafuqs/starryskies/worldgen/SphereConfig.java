package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.floatprovider.*;
import net.minecraft.util.math.random.Random;

import java.util.*;

public class SphereConfig {
	
	public static final MapCodec<SphereConfig> CONFIG_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
			FloatProvider.createValidatedCodec(1.0F, 64.0F).fieldOf("size").forGetter(sphereConfig -> sphereConfig.size),
			new Support.FailSoftMapCodec<>(ConfiguredSphereDecorator.CODEC, Codec.FLOAT).fieldOf("decorators").forGetter(sphereConfig -> sphereConfig.decorators),
			SphereEntitySpawnDefinition.CODEC.listOf().fieldOf("spawns").forGetter(sphereConfig -> sphereConfig.spawns),
			Generation.CODEC.optionalFieldOf("generation").forGetter(sphereConfig -> sphereConfig.generation)
	).apply(instance, SphereConfig::new));
	
	public record Generation(Identifier group, float weight) {
		public static final Codec<Generation> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Identifier.CODEC.fieldOf("group").forGetter(Generation::group),
						Codec.FLOAT.fieldOf("weight").forGetter(Generation::weight)
				).apply(instance, Generation::new)
		);
	}
	
	public final FloatProvider size;
	public final Map<ConfiguredSphereDecorator<?, ?>, Float> decorators;
	public final List<SphereEntitySpawnDefinition> spawns;
	public final Optional<Generation> generation;
	
	public SphereConfig(FloatProvider size, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators, List<SphereEntitySpawnDefinition> spawns, Optional<Generation> generation) {
		this.size = size;
		this.decorators = decorators;
		this.spawns = spawns;
		this.generation = generation;
	}
	
	DefaultSphereConfig DEFAULT = DefaultSphereConfig.INSTANCE;

	static float randomBetween(net.minecraft.util.math.random.Random random, int min, int max) {
		return min + random.nextFloat() * (max - min);
	}

	List<ConfiguredSphereDecorator<?, ?>> selectDecorators(net.minecraft.util.math.random.Random random) {
		List<ConfiguredSphereDecorator<?, ?>> result = new ArrayList<>();
		for (Map.Entry<ConfiguredSphereDecorator<?, ?>, Float> entry : decorators.entrySet()) {
			if (random.nextFloat() < entry.getValue()) {
				result.add(entry.getKey());
			}
		}
		return result;
	}

	List<Pair<EntityType<?>, Integer>> selectSpawns(Random random) {
		List<Pair<EntityType<?>, Integer>> result = new ArrayList<>();
		for (SphereEntitySpawnDefinition entry : spawns) {
			if (random.nextFloat() < entry.chance) {
				int count = Support.getRandomBetween(random, entry.minCount, entry.maxCount);
				result.add(new Pair<>(entry.entityType, count));
			}
		}
		return result;
	}
	
}
