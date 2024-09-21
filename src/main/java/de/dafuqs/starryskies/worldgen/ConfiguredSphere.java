package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.*;

import java.util.*;
import java.util.function.*;

public abstract class ConfiguredSphere<C> {

	protected final int minSize;
	protected final int maxSize;
	protected final Map<ConfiguredSphereDecorator<?, ?>, Float> decorators;
	protected final List<SphereEntitySpawnDefinition> spawns;

	public ConfiguredSphere(SharedConfig sharedConfig) {
		this.minSize = sharedConfig.minSize;
		this.maxSize = sharedConfig.maxSize;
		this.decorators = sharedConfig.decorators;
		this.spawns = sharedConfig.spawns;
	}

	protected static <C, P extends ConfiguredSphere<C>> MapCodec<P> createCodec(MapCodec<C> configCodec, BiFunction<SharedConfig, C, P> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				SharedConfig.CODEC.forGetter(ConfiguredSphere::sharedConfig),
				configCodec.fieldOf("type_data").forGetter(ConfiguredSphere::config)
		).apply(instance, constructor));
	}

	protected static float randomBetween(net.minecraft.util.math.random.Random random, int min, int max) {
		return min + random.nextFloat() * (max - min);
	}

	public abstract Spheres<? extends ConfiguredSphere<C>> getType();

	public SharedConfig sharedConfig() {
		return new SharedConfig(minSize, maxSize, decorators, spawns);
	}

	public abstract C config();

	protected List<ConfiguredSphereDecorator<?, ?>> selectDecorators(net.minecraft.util.math.random.Random random) {
		List<ConfiguredSphereDecorator<?, ?>> decorators = new ArrayList<>();
		for (Map.Entry<ConfiguredSphereDecorator<?, ?>, Float> entry : this.decorators.entrySet()) {
			if (random.nextFloat() < entry.getValue()) {
				decorators.add(entry.getKey());
			}
		}
		return decorators;
	}

	protected List<Pair<EntityType<?>, Integer>> selectSpawns(Random random) {
		List<Pair<EntityType<?>, Integer>> spawns = new ArrayList<>();
		for (SphereEntitySpawnDefinition entry : this.spawns) {
			if (random.nextFloat() < entry.chance) {
				int count = Support.getRandomBetween(random, entry.minCount, entry.maxCount);
				spawns.add(new Pair<>(entry.entityType, count));
			}
		}
		return spawns;
	}

	public abstract PlacedSphere generate(ChunkRandom systemRandom, DynamicRegistryManager registryManager);

	public record SharedConfig(int minSize, int maxSize, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators,
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
