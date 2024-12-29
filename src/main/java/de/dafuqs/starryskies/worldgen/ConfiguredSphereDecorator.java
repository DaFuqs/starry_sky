package de.dafuqs.starryskies.worldgen;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import net.minecraft.registry.entry.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public record ConfiguredSphereDecorator<FC extends SphereDecoratorConfig, F extends SphereDecorator<FC>>(F feature,
																										 FC config) {

	public static final Codec<ConfiguredSphereDecorator<?, ?>> CODEC = StarryRegistries.SPHERE_DECORATOR.getCodec().dispatch((f) -> f.feature, SphereDecorator::getCodec);
	public static final Codec<RegistryEntry<ConfiguredSphereDecorator<?, ?>>> REGISTRY_CODEC = RegistryElementCodec.of(StarryRegistryKeys.CONFIGURED_SPHERE_DECORATOR, CODEC);
	
	public boolean generate(StructureWorldAccess world, net.minecraft.util.math.random.Random random, BlockPos pos, PlacedSphere<?> sphere) {
		return this.feature.generateIfValid(this.config, world, random, pos, sphere);
	}

	public String toString() {
		return "Decorator: " + this.feature + ": " + this.config;
	}

	public F feature() {
		return this.feature;
	}

	public FC config() {
		return this.config;
	}
}