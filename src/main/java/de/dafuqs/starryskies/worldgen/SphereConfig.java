package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.random.*;
import net.minecraft.util.math.random.Random;

import java.util.*;

public interface SphereConfig {

	DefaultSphereConfig DEFAULT = DefaultSphereConfig.INSTANCE;

	static float randomBetween(net.minecraft.util.math.random.Random random, int min, int max) {
		return min + random.nextFloat() * (max - min);
	}

	static List<ConfiguredSphereDecorator<?, ?>> selectDecorators(net.minecraft.util.math.random.Random random, SharedConfig sharedConfig) {
		List<ConfiguredSphereDecorator<?, ?>> decorators = new ArrayList<>();
		for (Map.Entry<ConfiguredSphereDecorator<?, ?>, Float> entry : sharedConfig.decorators().entrySet()) {
			if (random.nextFloat() < entry.getValue()) {
				decorators.add(entry.getKey());
			}
		}
		return decorators;
	}

	static List<Pair<EntityType<?>, Integer>> selectSpawns(Random random, SharedConfig sharedConfig) {
		List<Pair<EntityType<?>, Integer>> spawns = new ArrayList<>();
		for (SphereEntitySpawnDefinition entry : sharedConfig.spawns()) {
			if (random.nextFloat() < entry.chance) {
				int count = Support.getRandomBetween(random, entry.minCount, entry.maxCount);
				spawns.add(new Pair<>(entry.entityType, count));
			}
		}
		return spawns;
	}

	SharedConfig config();

	default float getSize(ChunkRandom random) {
		return SphereConfig.randomBetween(random, config().minSize(), config().maxSize());
	}

	default List<ConfiguredSphereDecorator<?, ?>> getDecorators(ChunkRandom random) {
		return SphereConfig.selectDecorators(random, config());
	}

	default List<Pair<EntityType<?>, Integer>> getSpawns(ChunkRandom random) {
		return SphereConfig.selectSpawns(random, config());
	}

	record SharedConfig(int minSize, int maxSize, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators,
							   List<SphereEntitySpawnDefinition> spawns) {
		public static final MapCodec<SharedConfig> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						Codec.INT.fieldOf("min_size").forGetter(SharedConfig::minSize),
						Codec.INT.fieldOf("max_size").forGetter(SharedConfig::maxSize),
						new Support.FailSoftMapCodec<>(ConfiguredSphereDecorator.CODEC, Codec.FLOAT).fieldOf("decorators").forGetter(SharedConfig::decorators),
						SphereEntitySpawnDefinition.CODEC.listOf().fieldOf("spawns").forGetter(SharedConfig::spawns)
				).apply(instance, SharedConfig::new)
		);

	}
}
