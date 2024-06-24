package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.data_loaders.*;
import de.dafuqs.starryskies.dimension.*;
import de.dafuqs.starryskies.spheroids.decoration.*;
import de.dafuqs.starryskies.spheroids.spheroids.*;
import net.minecraft.registry.*;

public class StarryRegistryKeys {
	
	public static final RegistryKey<Registry<SystemGenerator>> SYSTEM_GENERATOR = of("starry_skies/system_generator");
	//public static final RegistryKey<Registry<SpheroidDistributionTypeLoader.SpheroidDistributionType>> SPHEROID_DISTRIBUTION_TYPE = of("starry_skies/distribution_type");
	
	public static final RegistryKey<Registry<SpheroidFeature<?>>> SPHEROID_FEATURE = of("starry_skies/sphere_decorator_types");
	public static final RegistryKey<Registry<ConfiguredSpheroidFeature<?, ?>>> CONFIGURED_SPHEROID_FEATURE = of("starry_skies/sphere_decorators");
	public static final RegistryKey<Registry<SpheroidTemplateType<?>>> SPHEROID_TEMPLATE_TYPE = of("starry_skies/sphere_types");
	public static final RegistryKey<Registry<Spheroid.Template<?>>> SPHEROID_TEMPLATE = of("starry_skies/spheres");
	public static final RegistryKey<Registry<StarryStateProvider>> STATE_PROVIDERS = of("starry_skies/state_providers");
	
	private static <T> RegistryKey<Registry<T>> of(String id) {
		return RegistryKey.ofRegistry(StarrySkies.locate(id));
	}
	
}
