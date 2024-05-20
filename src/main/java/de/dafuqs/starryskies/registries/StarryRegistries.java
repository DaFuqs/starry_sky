package de.dafuqs.starryskies.registries;

import de.dafuqs.starryskies.data_loaders.*;
import net.fabricmc.fabric.api.event.registry.*;
import net.minecraft.registry.*;

public class StarryRegistries {
	
	public static final Registry<SpheroidDimensionType> STARRY_DIMENSION_TYPE = create(StarryRegistryKeys.STARRY_DIMENSION_TYPE);
	public static final Registry<SpheroidDistributionLoader.SpheroidDistributionType> SPHEROID_DISTRIBUTION_TYPE = create(StarryRegistryKeys.SPHEROID_DISTRIBUTION_TYPE);
	public static final Registry<SpheroidDecoratorType> SPHEROID_DECORATOR_TYPE = create(StarryRegistryKeys.SPHEROID_DECORATOR_TYPE);
	public static final Registry<SpheroidTemplateType> SPHEROID_TEMPLATE = create(StarryRegistryKeys.SPHEROID_TEMPLATE);
	
	public static final Registry<UniqueBlockGroupsLoader.UniqueBlockGroup> UNIQUE_BLOCK_GROUP = create(StarryRegistryKeys.UNIQUE_BLOCK_GROUP);
	public static final Registry<WeightedBlockGroupsLoader.WeightedBlockGroup> WEIGHTED_BLOCK_GROUP = create(StarryRegistryKeys.WEIGHTED_BLOCK_GROUP);
	
	public static void register() {
	
	}
	
	public static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
		return FabricRegistryBuilder.createSimple(key).attribute(RegistryAttribute.MODDED).buildAndRegister();
	}
	
}
