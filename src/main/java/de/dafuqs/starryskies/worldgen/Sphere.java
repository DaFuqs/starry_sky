package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;
import net.minecraft.registry.*;
import net.minecraft.util.math.random.*;

public abstract class Sphere<SC extends SphereConfig> {

	private final MapCodec<ConfiguredSphere<Sphere<SC>, SC>> codec;

	public Sphere(Codec<SC> configCodec) {
		this.codec = configCodec.fieldOf("config")
				.xmap((config) -> new ConfiguredSphere<>(this, config), ConfiguredSphere::config);
	}

	public abstract PlacedSphere generate(ConfiguredSphere<? extends Sphere<SC>, SC> configuredSphere, SC config, ChunkRandom random, DynamicRegistryManager registryManager);

	public MapCodec<ConfiguredSphere<Sphere<SC>, SC>> getCodec() {
		return this.codec;
	}

}
