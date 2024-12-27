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
	
	public List<ConfiguredSphereDecorator<?, ?>> getDecorators(ChunkRandom random) {
		return config.selectDecorators(random);
	}
	
	public List<Pair<EntityType<?>, Integer>> getSpawns(ChunkRandom random) {
		return config.selectSpawns(random);
	}

	public PlacedSphere<?> generate(ChunkRandom systemRandom, DynamicRegistryManager registryManager) {
		return this.sphere.generate(this, this.config, systemRandom, registryManager);
	}
}
