package de.dafuqs.starryskies.spheroids.decoration;

import com.mojang.serialization.*;
import de.dafuqs.starryskies.registries.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public record ConfiguredSpheroidFeature<FC extends SpheroidFeatureConfig, F extends SpheroidFeature<FC>>(F feature, FC config) {
	
	public static final Codec<ConfiguredSpheroidFeature<?, ?>> CODEC = StarryRegistries.SPHEROID_FEATURE.getCodec().dispatch((f) -> f.feature, SpheroidFeature::getCodec);
	public static final Codec<RegistryEntry<ConfiguredSpheroidFeature<?, ?>>> REGISTRY_CODEC = RegistryElementCodec.of(StarryRegistryKeys.CONFIGURED_SPHEROID_FEATURE, CODEC);
	public static final Codec<RegistryEntryList<ConfiguredSpheroidFeature<?, ?>>> LIST_CODEC = RegistryCodecs.entryList(StarryRegistryKeys.CONFIGURED_SPHEROID_FEATURE, CODEC);
	
	public boolean generate(StructureWorldAccess world, net.minecraft.util.math.random.Random random, BlockPos pos, Spheroid spheroid) {
		return this.feature.generateIfValid(this.config, world, random, pos, spheroid);
	}
	
	public String toString() {
		String var10000 = String.valueOf(this.feature);
		return "Configured: " + var10000 + ": " + this.config;
	}
	
	public F feature() {
		return this.feature;
	}
	
	public FC config() {
		return this.config;
	}
}