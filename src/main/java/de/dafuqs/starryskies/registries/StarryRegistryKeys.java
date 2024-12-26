package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.data_loaders.*;
import de.dafuqs.starryskies.worldgen.SphereDecorator;
import de.dafuqs.starryskies.worldgen.*;
import de.dafuqs.starryskies.worldgen.dimension.*;
import net.minecraft.registry.*;

public class StarryRegistryKeys {
	
	// Builtin Registries
	public static final RegistryKey<Registry<GenerationGroup>> GENERATION_GROUP = of("starry_skies/generation_group");
	public static final RegistryKey<Registry<SphereDecorator<?>>> SPHERE_DECORATOR = of("starry_skies/sphere_decorator");
	
	// Dynamic Registries
	public static final RegistryKey<Registry<SystemGenerator>> SYSTEM_GENERATOR = of("starry_skies/system_generator");
	public static final RegistryKey<Registry<ConfiguredSphereDecorator<?, ?>>> CONFIGURED_SPHERE_DECORATOR = of("starry_skies/configured_decorator");
	public static final RegistryKey<Registry<Sphere<?>>> SPHERE = of("starry_skies/sphere_type");
	public static final RegistryKey<Registry<ConfiguredSphere<?, ?>>> CONFIGURED_SPHERE = of("starry_skies/configured_sphere");
	public static final RegistryKey<Registry<StarryStateProvider>> STATE_PROVIDER = of("starry_skies/state_provider");

	private static <T> RegistryKey<Registry<T>> of(String id) {
		return RegistryKey.ofRegistry(StarrySkies.locate(id));
	}

}
