package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.worldgen.spheres.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.random.*;

import java.util.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public class ConfiguredSphere<S extends Sphere<SC>, SC extends SphereConfig> {

	public static final Codec<ConfiguredSphere<?, ?>> CODEC = StarryRegistries.SPHERE.getCodec().dispatch((configuredSphere) -> configuredSphere.sphere, Sphere::getCodec);

	protected final S sphere;
	protected final SharedConfig sharedConfig;
	protected final Optional<Generation> generation;
	protected final SC config;

	public ConfiguredSphere(S sphere, SC config, SharedConfig sharedConfig, Optional<Generation> generation) {
		this.sphere = sphere;
		this.config = config;
		this.sharedConfig = sharedConfig;
		this.generation = generation;
	}
	
	public SharedConfig sharedConfig() {
		return sharedConfig;
	}
	
	public Optional<Generation> generation() {
		return generation;
	}

	public SC config() {
		return config;
	}
	
	public float getSize(ChunkRandom random) {
		return SphereConfig.randomBetween(random, sharedConfig().minSize(), sharedConfig().maxSize());
	}
	
	public List<ConfiguredSphereDecorator<?, ?>> getDecorators(ChunkRandom random) {
		return SphereConfig.selectDecorators(random, sharedConfig());
	}
	
	public List<Pair<EntityType<?>, Integer>> getSpawns(ChunkRandom random) {
		return SphereConfig.selectSpawns(random, sharedConfig());
	}

	public PlacedSphere<? extends Sphere<SC>> generate(ChunkRandom systemRandom, DynamicRegistryManager registryManager) {
		return this.sphere.generate(this, this.config, systemRandom, registryManager);
	}
	
	public record SharedConfig(int minSize, int maxSize, Map<ConfiguredSphereDecorator<?, ?>, Float> decorators, List<SphereEntitySpawnDefinition> spawns) {
		public static final MapCodec<SharedConfig> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						Codec.INT.fieldOf("min_size").forGetter(SharedConfig::minSize),
						Codec.INT.fieldOf("max_size").forGetter(SharedConfig::maxSize),
						new Support.FailSoftMapCodec<>(ConfiguredSphereDecorator.CODEC, Codec.FLOAT).fieldOf("decorators").forGetter(SharedConfig::decorators),
						SphereEntitySpawnDefinition.CODEC.listOf().fieldOf("spawns").forGetter(SharedConfig::spawns)
				).apply(instance, SharedConfig::new)
		);
	}
	
	public record Generation(Identifier group, float weight) {
		public static final Codec<Generation> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Identifier.CODEC.fieldOf("group").forGetter(Generation::group),
						Codec.FLOAT.fieldOf("weight").forGetter(Generation::weight)
				).apply(instance, Generation::new)
		);

	}
}
