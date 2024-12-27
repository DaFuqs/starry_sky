package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import de.dafuqs.starryskies.worldgen.spheres.*;
import net.minecraft.registry.*;
import net.minecraft.util.math.random.*;

import java.util.*;

import static de.dafuqs.starryskies.Support.BLOCKSTATE_STRING_CODEC;

public abstract class Sphere<SC extends SphereConfig> {

	private final MapCodec<ConfiguredSphere<Sphere<SC>, SC>> codec;

	public Sphere(Codec<SC> configCodec) {
		this.codec = configCodec.fieldOf("type_data").xmap((config, sharedConfig, generation) -> configure(config, sharedConfig, generation), ConfiguredSphere::config, ConfiguredSphere::sharedConfig, ConfiguredSphere::generation);
	}
	
	public ConfiguredSphere<Sphere<SC>, SC> configure(SC config, ConfiguredSphere.SharedConfig sharedConfig, Optional<ConfiguredSphere.Generation> generation) {
		return new ConfiguredSphere<>(this, config, sharedConfig, generation);
	}
	
	public abstract PlacedSphere<? extends Sphere<SC>> generate(ConfiguredSphere<? extends Sphere<SC>, SC> configuredSphere, SC config, ChunkRandom random, DynamicRegistryManager registryManager);

	public MapCodec<ConfiguredSphere<Sphere<SC>, SC>> getCodec() {
		return this.codec;
	}

}
