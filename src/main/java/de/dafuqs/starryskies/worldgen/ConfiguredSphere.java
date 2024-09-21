package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.registries.*;
import net.minecraft.registry.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.feature.*;

import java.util.*;
import java.util.function.*;

public class ConfiguredSphere<S extends Sphere<SC>, SC extends SphereConfig> {

	public static final Codec<ConfiguredSphere<?, ?>> CODEC = StarryRegistries.SPHERE
			.getCodec().dispatch((configuredSphere) -> configuredSphere.sphere, Sphere::getCodec);

	protected final S sphere;
	protected final SC config;

	public ConfiguredSphere(S sphere, SC config) {
		this.sphere = sphere;
		this.config = config;
	}

	public SC config() {
		return config;
	}


	public PlacedSphere<? extends Sphere<SC>> generate(ChunkRandom systemRandom, DynamicRegistryManager registryManager) {
		return this.sphere.generate(this, this.config, systemRandom, registryManager);
	}

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
