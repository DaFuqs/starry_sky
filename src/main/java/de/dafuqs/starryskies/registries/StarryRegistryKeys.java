package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.worldgen.*;
import de.dafuqs.starryskies.worldgen.dimension.*;
import net.minecraft.registry.*;

public class StarryRegistryKeys {
	
	// Builtin Registries
	public static final RegistryKey<Registry<Sphere<?>>> SPHERE = of("sphere_type");
	public static final RegistryKey<Registry<SphereDecorator<?>>> SPHERE_DECORATOR = of("sphere_decorator");
	
	// Dynamic Registries
	public static final RegistryKey<Registry<ConfiguredSphereDecorator<?, ?>>> CONFIGURED_SPHERE_DECORATOR = of("configured_decorator");
	public static final RegistryKey<Registry<ConfiguredSphere<?, ?>>> CONFIGURED_SPHERE = of("configured_sphere");
	public static final RegistryKey<Registry<GenerationGroup>> GENERATION_GROUP = of("generation_group");
	public static final RegistryKey<Registry<SystemGenerator>> SYSTEM_GENERATOR = of("system_generator");
	
	private static <T> RegistryKey<Registry<T>> of(String name) {
		return RegistryKey.ofRegistry(StarrySkies.id(name));
	}

}
