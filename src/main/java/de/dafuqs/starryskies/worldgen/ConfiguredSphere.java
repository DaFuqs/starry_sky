package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import net.minecraft.registry.*;
import net.minecraft.util.math.random.*;

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

}
