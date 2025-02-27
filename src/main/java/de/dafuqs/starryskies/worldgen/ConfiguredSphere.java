package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import net.minecraft.entity.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;

import java.util.*;

public class ConfiguredSphere<S extends Sphere<SC>, SC extends SphereConfig> {
	
	public static final Codec<ConfiguredSphere<?, ?>> CODEC = StarryRegistries.SPHERE.getCodec().dispatch((configuredSphere) -> configuredSphere.sphere, Sphere::getCodec);

	protected final S sphere;
	protected final SC config;

	public ConfiguredSphere(S sphere, SC config) {
		this.sphere = sphere;
		this.config = config;
	}

	public SC config() {
		return config;
	}
	
	public float getSize(ChunkRandom random) {
		return config.size.get(random);
	}
	
	public List<RegistryEntry<ConfiguredSphereDecorator<?, ?>>> getDecorators(ChunkRandom random) {
		return config.selectDecorators(random);
	}
	
	public List<Pair<EntityType<?>, Integer>> getSpawns(ChunkRandom random) {
		return config.selectSpawns(random);
	}
	
	public Optional<SphereConfig.Generation> getGenerationGroup() {
		return config.generation;
	}
	
	public PlacedSphere<?> generate(ChunkRandom systemRandom, DynamicRegistryManager registryManager, BlockPos pos, float radius) {
		return this.sphere.generate(this, this.config, systemRandom, registryManager, pos, radius);
	}
}
