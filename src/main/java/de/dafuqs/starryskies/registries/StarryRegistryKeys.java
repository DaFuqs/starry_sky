package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.*;
import de.dafuqs.starryskies.data_loaders.*;
import net.minecraft.registry.*;

public class StarryRegistryKeys {
	
	public static final RegistryKey<Registry<SpheroidDimensionType>> STARRY_DIMENSION_TYPE = of("starry_skies/dimension_type");
	public static final RegistryKey<Registry<SpheroidDistributionLoader.SpheroidDistributionType>> SPHEROID_DISTRIBUTION_TYPE = of("starry_skies/distribution_type");
	public static final RegistryKey<Registry<SpheroidDecoratorType<?>>> SPHEROID_DECORATOR_TYPE = of("starry_skies/sphere_decorators");
	public static final RegistryKey<Registry<SpheroidTemplateType<?>>> SPHEROID_TEMPLATE = of("starry_skies/spheres");
	public static final RegistryKey<Registry<UniqueBlockGroupsLoader.UniqueBlockGroup>> UNIQUE_BLOCK_GROUP = of("starry_skies/unique_block_groups");
	public static final RegistryKey<Registry<WeightedBlockGroupsLoader.WeightedBlockGroup>> WEIGHTED_BLOCK_GROUP = of("starry_skies/weighted_block_groups");
	
	private static <T> RegistryKey<Registry<T>> of(String id) {
		return RegistryKey.ofRegistry(StarrySkies.locate(id));
	}
	
}
